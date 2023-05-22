package com.bysj_backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bysj_backend.entity.User;
import com.bysj_backend.service.UserService;
import com.bysj_backend.utils.Check;
import com.bysj_backend.utils.Constants;
import com.bysj_backend.utils.OperationResult;
import com.bysj_backend.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录
     *
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/login")
    public OperationResult<String> login(HttpServletRequest request, @RequestBody User user) {

        log.info("进入用户登录方法");
        OperationResult<String> operationResult = new OperationResult<>();
        try {

            if (StringUtils.isBlank(user.getLoginName())) {
                operationResult.setMessage("请输入用户名");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            if (StringUtils.isBlank(user.getPassword())) {
                operationResult.setMessage("请输入密码");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //1.将页面提交的密码password进行md5加密处理
            String password = user.getPassword();
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            //2.根据页面提交的用户名username查询数据库
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getLoginName, user.getLoginName());
            User loginUser = userService.getOne(queryWrapper);

            //3.如果没有查询到则返回登录失败结果
            if (loginUser == null) {
                operationResult.setMessage("登录失败,没有该用户");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            //4.密码比对,如果不一致则返回登录失败结果
            if (!loginUser.getPassword().equals(password)) {
                operationResult.setMessage("登录失败,密码错误");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            //5.登录成功,将用户id存入Session并返回登录成功结果
            log.info("当前用户id为:  " + loginUser.getId());
            request.getSession().setAttribute("user", loginUser.getId());
            operationResult.setMessage("登录成功,当前用户是:  " + loginUser.getLoginName());
            operationResult.setStatus(OperationResult.STATUS_SUCCESS);
        } catch (Exception e) {
            log.info("login方法出现异常:  " + e.getMessage());
            operationResult.setMessage("登录失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public OperationResult<String> logout(HttpServletRequest request) {

        OperationResult<String> operationResult = new OperationResult<>();
        try {
            if (request.getSession().getAttribute("user") != null) {
                log.info("当前用户id,即将退出登录: " + request.getSession().getAttribute("user"));
                //清理Session中保存的当前登录员工的id
                request.getSession().removeAttribute("user");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                operationResult.setMessage("退出成功");
            } else {
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setMessage("当前未登录");
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return operationResult;
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("/saveUser")
    public OperationResult<String> save(@RequestBody User user) {
        log.info("新增,用户信息: {}", user.toString());
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getLoginName()) || StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getPhone()) ||
                StringUtils.isBlank(user.getIdCard())){

                operationResult.setMessage("请将信息填写完整");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return  operationResult;
            }
            //校验手机号码
            Boolean mobile = Check.isMobile(user.getPhone());
            if (!mobile){
                operationResult.setMessage("手机号码格式错误");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //校验身份证是否合法
            String result = Check.IDCardValidate(user.getIdCard());
            if (!result.equals("该身份证有效！")){
                operationResult.setMessage(result);
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //设置唯一id
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("生成唯一id: " + id);
            user.setId(id);
            //设置初始密码 用MD5加密
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

            if (user.getSex() != null) {
                if (Constants.SEX_MAN.equals(user.getSex())) {  //1为男
                    user.setSex("男");
                } else if (Constants.SEX_WOMAN.equals(user.getSex())) { //0为女
                    user.setSex("女");
                }
            }
            log.info("user: " + user);
            boolean checkFlag = false;
            //查询该登录名是否已存在
            LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(User::getLoginName, user.getLoginName());
            User loginUserName = userService.getOne(queryWrapper1);

            //查询该手机号是否已被注册
            LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(User::getPhone, user.getPhone());
            User loginUserPhone = userService.getOne(queryWrapper2);

            //查询该身份证是否已被注册
            LambdaQueryWrapper<User> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.eq(User::getIdCard, user.getIdCard());
            User loginUserIdCard = userService.getOne(queryWrapper3);

            if (loginUserName != null) {
                operationResult.setMessage("用户已存在");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            } else if (loginUserPhone != null) {
                operationResult.setMessage("该手机号已被注册");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            } else if (loginUserIdCard != null) {
                operationResult.setMessage("该身份证已被注册");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            } else {
                checkFlag = userService.save(user);
                log.info("checkFlag: " + checkFlag);
            }

            if (checkFlag) {
                operationResult.setMessage("新增用户成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("新增用户失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("新增用户失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @PostMapping("/updateUser")
    public OperationResult<String> modify(@RequestBody User user) {
        log.info("进入修改用户方法");
        log.info("user :" + user);
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            boolean checkFlag = false;

            //查询要修改的登录名是否已存在
            if (StringUtils.isNotEmpty(user.getLoginName())) {
                LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(User::getLoginName, user.getLoginName());
                User loginUserName = userService.getOne(queryWrapper1);

                if (loginUserName != null) {
                    operationResult.setMessage("用户已存在");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            }
            checkFlag = userService.updateById(user);
            log.info("checkFlag: " + checkFlag);

            if (checkFlag) {
                operationResult.setMessage("用户修改成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("用户修改失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("更新用户方法出现异常: " + e.getMessage());
            operationResult.setMessage("用户修改失败，出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    @GetMapping("/queryUser")
    public OperationResult<JSON> query(HttpSession session) {
        log.info("进入查询用户信息方法");
        OperationResult<JSON> operationResult = new OperationResult<>();
        try {
            String userId = (String) session.getAttribute("user");
            if (userId == null) {
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getId, userId);
            User user = userService.getOne(queryWrapper);
            if (user != null) {
                operationResult.setMessage("查询成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                operationResult.setData((JSON) JSONObject.toJSON(user));
            } else {
                operationResult.setMessage("未查询到数据");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }


        } catch (Exception e) {
            log.info("出现异常: " + e.getMessage());
            operationResult.setMessage("查询用户信息失败，出现异常");
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
            String userId = (String) session.getAttribute("user");
            if (userId != null) {
                log.info("当前用户Id: " + userId);
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getId, userId);
                User user = userService.getOne(queryWrapper);
                //从redis中获取保存的验证码
                String redisCode = stringRedisTemplate.opsForValue().get("update:code" + user.getLoginName());
                log.info("从redis中查出的验证码: " + code);
                //进行验证码的比对
                if (redisCode != null && redisCode.equals(code)) {
                    user.setPhone(phone);
                    checkFlag = userService.updateById(user);
                } else {
                    operationResult.setMessage("验证码错误,请重新输入");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            } else {
                log.info("请先登录");
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
            String userId = (String) session.getAttribute("user");
            if (userId != null) {
                log.info("当前用户Id: " + userId);
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getId, userId);
                User user = userService.getOne(queryWrapper);
                //从redis中获取保存的验证码
                String redisCode = stringRedisTemplate.opsForValue().get("update:code" + user.getLoginName());
                log.info("从redis中查出的验证码: " + code);
                //进行验证码的比对
                if (redisCode != null && redisCode.equals(code)) {
                    user.setIdCard(idCard);
                    checkFlag = userService.updateById(user);
                } else {
                    operationResult.setMessage("验证码错误,请重新输入");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            } else {
                log.info("请先登录");
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
            String userId = (String) session.getAttribute("user");
            if (userId != null) {
                log.info("当前用户Id: " + userId);
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getId, userId);
                User user = userService.getOne(queryWrapper);
                //从redis中获取保存的验证码
                String redisCode = stringRedisTemplate.opsForValue().get("update:code" + user.getLoginName());
                log.info("从redis中查出的验证码: " + code);
                //进行验证码的比对
                if (redisCode != null && redisCode.equals(code)) {
                    user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
                    checkFlag = userService.updateById(user);
                } else {
                    operationResult.setMessage("验证码错误,请重新输入");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
            } else {
                log.info("请先登录");
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
            String userId = (String) session.getAttribute("user");
            if (StringUtils.isNotEmpty(userId)) {
                log.info("当前用户Id: " + userId);
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getId, userId);
                User user = userService.getOne(queryWrapper);
                //生成随机的4为验证码
                String code = ValidateCodeUtils.generateValidateCode(4).toString();
                log.info("code={}", code); // 在控制台打印 模仿收到验证码

                //保存验证码到redis
                stringRedisTemplate.opsForValue().set("update:code" + user.getLoginName(), code, 2, TimeUnit.MINUTES);

                operationResult.setMessage("验证码发送成功,请在2分钟内进行校验");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                log.info("请先登录");
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
