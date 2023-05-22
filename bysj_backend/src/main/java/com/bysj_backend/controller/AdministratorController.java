package com.bysj_backend.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bysj_backend.entity.Administrator;
import com.bysj_backend.entity.User;
import com.bysj_backend.service.AdministratorService;
import com.bysj_backend.service.UserService;
import com.bysj_backend.utils.Check;
import com.bysj_backend.utils.Constants;
import com.bysj_backend.utils.OperationResult;
import com.bysj_backend.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/administrator")
public class AdministratorController {


    @Autowired
    private AdministratorService administratorService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 管理员登录
     * @param request
     * @param administrator
     * @return
     */
    @PostMapping("/login")
    public OperationResult<String> login(HttpServletRequest request, @RequestBody Administrator administrator){

        log.info("进入管理员登录方法");
        OperationResult<String> operationResult = new OperationResult<>();
        try{

            if (StringUtils.isBlank(administrator.getLoginName())){
                operationResult.setMessage("请输入用户名");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            if (StringUtils.isBlank(administrator.getPassword())){
                operationResult.setMessage("请输入密码");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }



            //1.将页面提交的密码password进行md5加密处理
            String password = administrator.getPassword();
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            //2.根据页面提交的用户名username查询数据库
            LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Administrator::getLoginName,administrator.getLoginName());
            Administrator loginAdministrator = administratorService.getOne(queryWrapper);

            //3.如果没有查询到则返回登录失败结果
            if (loginAdministrator==null){
                operationResult.setMessage("登录失败,没有该管理员");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            //4.密码比对,如果不一致则返回登录失败结果
            if (!loginAdministrator.getPassword().equals(password)){
                operationResult.setMessage("登录失败,密码错误");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            //5.登录成功,将管理员id存入Session并返回登录成功结果
            log.info("当前管理员id为:  "+loginAdministrator.getId());
            request.getSession().setAttribute("administrator",loginAdministrator.getId());
            operationResult.setMessage("登录成功,当前管理员是: " + loginAdministrator.getLoginName());
            operationResult.setStatus(OperationResult.STATUS_SUCCESS);
        }catch (Exception e){
            log.info("login方法出现异常:  "+e.getMessage());
        }
        return  operationResult;
    }

    /**
     * 管理员退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public OperationResult<String> logout(HttpServletRequest request){

        OperationResult<String> operationResult = new OperationResult<>();
        try{
            if (request.getSession().getAttribute("administrator") != null){
                log.info("当前管理员id,即将退出登录: " + request.getSession().getAttribute("administrator"));
                //清理Session中保存的当前登录管理员的id
                request.getSession().removeAttribute("administrator");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                operationResult.setMessage("退出成功");
            }else {
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setMessage("当前未登录");
            }

        }catch (Exception e){
            log.info(e.getMessage());
        }
        return operationResult;
    }



    /**
     * 新增管理员
     * @param administrator
     * @return
     */
    @PostMapping("/saveAdministrator")
    public OperationResult<String> save(@RequestBody Administrator administrator){
        log.info("新增,管理员信息: {}",administrator.toString());
        OperationResult<String> operationResult = new OperationResult<>();
        try{

            if (StringUtils.isBlank(administrator.getName()) || StringUtils.isBlank(administrator.getLoginName()) || StringUtils.isBlank(administrator.getPassword()) ||
                StringUtils.isBlank(administrator.getPhone()) || StringUtils.isBlank(administrator.getIdCard())){

                operationResult.setMessage("请将信息填写完整");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return  operationResult;
            }
            //校验手机号码
            Boolean mobile = Check.isMobile(administrator.getPhone());
            if (!mobile){
                operationResult.setMessage("手机号码格式错误");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //校验身份证是否合法
            String result = Check.IDCardValidate(administrator.getIdCard());
            if (!result.equals("该身份证有效！")){
                operationResult.setMessage(result);
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //设置唯一id
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("生成唯一id: "+id);
            administrator.setId(id);
            //设置密码  用MD5加密
            administrator.setPassword(DigestUtils.md5DigestAsHex(administrator.getPassword().getBytes()));

            if (administrator.getSex() != null){
                if (Constants.SEX_MAN.equals(administrator.getSex())){  //1为男
                    administrator.setSex("男");
                }else if (Constants.SEX_WOMAN.equals(administrator.getSex())){ //0为女
                    administrator.setSex("女");
                }
            }

            boolean checkFlag = false;
            //查询该登录名是否已存在
            LambdaQueryWrapper<Administrator> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Administrator::getLoginName,administrator.getLoginName());
            Administrator loginAdministratorName = administratorService.getOne(queryWrapper1);

            //查询该手机号是否已被注册
            LambdaQueryWrapper<Administrator> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Administrator::getPhone,administrator.getPhone());
            Administrator loginAdministratorPhone = administratorService.getOne(queryWrapper2);
            //查询该身份证是否已被注册
            LambdaQueryWrapper<Administrator> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.eq(Administrator::getIdCard,administrator.getIdCard());
            Administrator loginAdministratorIdCard = administratorService.getOne(queryWrapper3);

            if (loginAdministratorName != null){
                operationResult.setMessage("用户已存在");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }else if (loginAdministratorPhone != null){
                operationResult.setMessage("该手机号已被注册");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }else if (loginAdministratorIdCard != null){
                operationResult.setMessage("该身份证已被注册");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            } else {
                checkFlag = administratorService.save(administrator);
                log.info("checkFlag: " + checkFlag);
            }

            if (checkFlag){
                operationResult.setMessage("新增管理员成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            }else {
                operationResult.setMessage("新增管理员失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }

        }catch (Exception e){
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("新增管理员失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 修改管理员信息
     * @param administrator
     * @return
     */
    @PostMapping("/updateAdministrator")
    public OperationResult<String> modify(@RequestBody Administrator administrator){
        log.info("进入修改管理员方法");
        log.info("administrator :" + administrator);
        OperationResult<String> operationResult = new OperationResult<>();
        try{
            boolean checkFlag = false;

            //查询要修改的登录名是否已存在
            if (StringUtils.isNotEmpty(administrator.getLoginName())) {
                LambdaQueryWrapper<Administrator> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(Administrator::getLoginName, administrator.getLoginName());
                Administrator loginAdminName = administratorService.getOne(queryWrapper1);

                if (loginAdminName != null) {
                    operationResult.setMessage("管理员已存在");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            }
            checkFlag = administratorService.updateById(administrator);
            log.info("checkFlag: " + checkFlag);

            if (checkFlag) {
                operationResult.setMessage("管理员信息修改成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("管理员信息修改失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        }catch (Exception e){
            log.info("更新管理员信息方法出现异常: " + e.getMessage());
            operationResult.setMessage("修改失败，出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    @GetMapping("/queryAdministrator")
    public OperationResult<JSON> query(HttpSession session){
        log.info("进入查询管理员信息方法");
        OperationResult<JSON> operationResult = new OperationResult<>();
        try {
            String administratorId = (String) session.getAttribute("administrator");
            if (administratorId == null){
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return  operationResult;
            }
            LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Administrator::getId,administratorId);
            Administrator administrator = administratorService.getOne(queryWrapper);
            if (administrator != null){
                operationResult.setMessage("查询成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                operationResult.setData((JSON) JSON.toJSON(administrator));
            }else {
                operationResult.setMessage("未查询到数据");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }


        }catch (Exception e){
            log.info("出现异常: " + e.getMessage());
            operationResult.setMessage("查询管理员信息失败，出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }



    /**
     * 修改手机号码
     *
     * @param phone   ,code
     * @param session
     * @return
     */
    @GetMapping("/updatePhone")
    public OperationResult<String> updatePhone(@RequestParam(required = false) String phone,
                                               @RequestParam(required = false) String code,
                                               HttpSession session) {
        log.info("进入修改手机号码方法");
        OperationResult<String> operationResult = new OperationResult<>();
        boolean checkFlag = false;
        try {
            log.info("phone-----" + phone);
            log.info("code-----" + code);
            String adminId = (String) session.getAttribute("administrator");
            if (adminId != null) {
                log.info("当前用户Id: " + adminId);
                LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Administrator::getId, adminId);
                Administrator administrator = administratorService.getOne(queryWrapper);
                //从redis中获取保存的验证码
                String redisCode = stringRedisTemplate.opsForValue().get("update:code" + administrator.getLoginName());
                log.info("从redis中查出的验证码: " + code);
                //进行验证码的比对
                if (redisCode != null && redisCode.equals(code)) {
                    administrator.setPhone(phone);
                    checkFlag = administratorService.updateById(administrator);
                } else {
                    operationResult.setMessage("验证码错误,请重新输入");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            } else {
                log.info("请先用管理员账号登录");
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }

            if (checkFlag) {
                operationResult.setMessage("手机号码修改成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("手机号码修改失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("修改手机号码方法出现异常: " + e);
            operationResult.setMessage("修改失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }



    /**
     * 修改身份证号码
     *
     * @param idCard  code
     * @param session
     * @return
     */
    @GetMapping("/updateIdCard")
    public OperationResult<String> updateIdCard(@RequestParam(required = false) String idCard,
                                                @RequestParam(required = false) String code,
                                                HttpSession session) {
        log.info("进入修改身份证方法");
        OperationResult<String> operationResult = new OperationResult<>();
        boolean checkFlag = false;
        try {

            log.info("idCard-----" + idCard);
            log.info("code-----" + code);
            String adminId = (String) session.getAttribute("administrator");
            if (adminId != null) {
                log.info("当前用户Id: " + adminId);
                LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Administrator::getId, adminId);
                Administrator administrator = administratorService.getOne(queryWrapper);
                //从redis中获取保存的验证码
                String redisCode = stringRedisTemplate.opsForValue().get("update:code" + administrator.getLoginName());
                log.info("从redis中查出的验证码: " + code);
                //进行验证码的比对
                if (redisCode != null && redisCode.equals(code)) {
                    administrator.setIdCard(idCard);
                    checkFlag = administratorService.updateById(administrator);
                } else {
                    operationResult.setMessage("验证码错误,请重新输入");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            } else {
                log.info("请先用管理员账号登录");
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }

            if (checkFlag) {
                operationResult.setMessage("身份证修改成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("身份证修改失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("修改身份证方法出现异常: " + e.getMessage());
            operationResult.setMessage("修改失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 修改密码
     *
     * @param password code
     * @param session
     * @return
     */
    @GetMapping("/updatePassword")
    public OperationResult<String> updatePassword(@RequestParam(required = false) String password,
                                                  @RequestParam(required = false) String code,
                                                  HttpSession session) {
        log.info("进入修改密码接口");
        OperationResult<String> operationResult = new OperationResult<>();
        boolean checkFlag = false;
        try {

            log.info("password-----" + password);
            log.info("code-----" + code);
            String adminId = (String) session.getAttribute("administrator");
            if (adminId != null) {
                log.info("当前管理员Id: " + adminId);
                LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Administrator::getId, adminId);
                Administrator administrator = administratorService.getOne(queryWrapper);
                //从redis中获取保存的验证码
                String redisCode = stringRedisTemplate.opsForValue().get("update:code" + administrator.getLoginName());
                log.info("从redis中查出的验证码: " + code);
                //进行验证码的比对
                if (redisCode != null && redisCode.equals(code)) {
                    administrator.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
                    checkFlag = administratorService.updateById(administrator);
                } else {
                    operationResult.setMessage("验证码错误,请重新输入");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            } else {
                log.info("请先用管理员账号登录");
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }

            if (checkFlag) {
                operationResult.setMessage("密码修改成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("密码修改失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("修改密码方法出现异常: " + e.getMessage());
            operationResult.setMessage("修改失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 模拟发送手机短信验证码(实际打到控制台)
     *
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public OperationResult<String> sendMsg(HttpSession session) {
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            String adminId = (String) session.getAttribute("administrator");
            if (StringUtils.isNotEmpty(adminId)) {
                log.info("当前管理员Id: " + adminId);
                LambdaQueryWrapper<Administrator> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Administrator::getId, adminId);
                Administrator administrator = administratorService.getOne(queryWrapper);
                //生成随机的4为验证码
                String code = ValidateCodeUtils.generateValidateCode(4).toString();
                log.info("code={}", code); // 在控制台打印 模仿收到验证码

                //保存验证码到redis
                stringRedisTemplate.opsForValue().set("update:code" + administrator.getLoginName(), code, 2, TimeUnit.MINUTES);

                operationResult.setMessage("验证码发送成功,请在2分钟内进行校验");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                log.info("请先用管理员账号登录");
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
        } catch (Exception e) {
            log.info("出现异常" + e);
            operationResult.setMessage("验证码发送失败");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }


        return operationResult;
    }



}
