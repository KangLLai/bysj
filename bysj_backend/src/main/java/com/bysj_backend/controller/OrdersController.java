package com.bysj_backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bysj_backend.entity.Orders;
import com.bysj_backend.service.AddressService;
import com.bysj_backend.service.OrdersService;
import com.bysj_backend.service.TicketService;
import com.bysj_backend.service.UserService;
import com.bysj_backend.utils.Check;
import com.bysj_backend.utils.Constants;
import com.bysj_backend.utils.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {


    @Autowired
    private OrdersService ordersService;

    /**
     * 新增订单
     * @param order
     * @return
     */
    @PostMapping("/saveOrder")
    public OperationResult<String> save(@RequestBody Orders order, HttpServletRequest request){
        log.info("进入新增订单方法save");
        OperationResult<String> operationResult = new OperationResult<>();
        try {

            if (StringUtils.isBlank(order.getViewName()) ||StringUtils.isBlank(order.getIdCard() )||
                    StringUtils.isBlank(order.getAddress()) || StringUtils.isBlank(order.getAddressName()) ||
                    StringUtils.isBlank(order.getPhone() )){

                operationResult.setMessage("请填写完整信息");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //设置用户id
            String userId = (String) request.getSession().getAttribute("user");
            if (userId !=null){
                order.setUserId(userId);
            }else {
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
            //设置订单号
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("生成唯一id: "+id);
            order.setOrderId(id);
            order.setStatus(Constants.ORDER_STATUS_1);

            //校验手机号码
            Boolean mobile = Check.isMobile(order.getPhone());
            if (!mobile){
                operationResult.setMessage("手机号码格式错误");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            //校验身份证是否合法
            String result = Check.IDCardValidate(order.getIdCard());
            if (!result.equals("该身份证有效！")){
                operationResult.setMessage(result);
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }

            log.info("order: " +order);
            //执行插入数据库操作
            boolean save = ordersService.save(order);
            if (save){
                operationResult.setMessage("下单成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            }else {
                operationResult.setMessage("下单失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        }catch (Exception e){
            log.info("异常信息:{}",e.getMessage());
            operationResult.setMessage("新增订单方法出现异常: " + e.getMessage());
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }

        return operationResult;
    }

    /**
     * 修改订单 (仅可修改收货信息,订单状态,备注信息)
     * @param order
     * @return
     */
    @PostMapping("/updateOrder")
    public OperationResult<String> modify(@RequestBody Orders order){
        log.info("进入修改订单信息方法");
        OperationResult<String> operationResult = new OperationResult<>();
        try {


            LambdaUpdateWrapper<Orders> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(Orders::getOrderId, order.getOrderId());



            if (StringUtils.isNotEmpty(order.getAddressId())){
                if (order.getStatus().equals(Constants.ORDER_STATUS_1)) {
                    //修改地址信息
                    queryWrapper.set(StringUtils.isNotEmpty(order.getAddressId()), Orders::getAddressId, order.getAddressId());
                    queryWrapper.set(StringUtils.isNotEmpty(order.getAddress()), Orders::getAddress, order.getAddress());
                    queryWrapper.set(StringUtils.isNotEmpty(order.getAddressName()), Orders::getAddressName, order.getAddressName());
                    //修改备注信息
                    queryWrapper.set(StringUtils.isNotEmpty(order.getRemark()), Orders::getRemark, order.getRemark());
                } else {
                    operationResult.setMessage("订单已派送/已取消/已完成,不支持修改");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                    return operationResult;
                }
        }
            //修改订单状态
            queryWrapper.set(StringUtils.isNotEmpty(order.getStatus()), Orders::getStatus, order.getStatus());
            boolean update = ordersService.update(queryWrapper);
            if (update){
                operationResult.setMessage("操作成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            }else {
                operationResult.setMessage("操作失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }

        }catch (Exception e){
            log.info("异常信息:{}",e.getMessage());
            operationResult.setMessage("修改订单方法出现异常: " + e.getMessage());
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 删除订单(可批量)
     * @param orderIds
     * @return
     */
    @PostMapping("/deleteOrder")
    public OperationResult<String> deleteOrders(@RequestBody List<String> orderIds){
        log.info("删除订单信息方法,ids: " + orderIds.toString());
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            boolean checkFlag = false;
            for (String orderId : orderIds) {
                LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Orders::getOrderId,orderId);
                checkFlag = ordersService.remove(queryWrapper);
            }
            if (checkFlag) {
                operationResult.setMessage("删除成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("删除失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("删除失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 查询指定用户订单(查询条件: 时间范围, 订单号)
     * @param session
     * @param pageIndex
     * @param pageSize
     * @param orderId
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/queryUserOrders")
    public OperationResult<JSONArray> queryUserOrders(HttpSession session,
                                                      @RequestParam(required = false) Integer pageIndex,
                                                      @RequestParam(required = false) Integer pageSize,
                                                      @RequestParam(required = false) String orderId,
                                                      @RequestParam(required = false) String beginTime,
                                                      @RequestParam(required = false) String endTime){
        log.info("进入查询指定用户订单方法");
        OperationResult<JSONArray> operationResult = new OperationResult<>();
        try {
            String userId = (String) session.getAttribute("user");
            if (userId == null){
                operationResult.setMessage("请先登录");
                operationResult.setErrorCode(Constants.NOTLOGIN);
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                return operationResult;
            }
            //构造条件查询对象
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

            //添加查询条件  动态sql
            //使用了范围查询的动态SQL
            queryWrapper.eq(StringUtils.isNotEmpty(userId),Orders::getUserId,userId)
                    .eq(StringUtils.isNotEmpty(orderId),Orders::getOrderId,orderId)
                    .gt(StringUtils.isNotEmpty(beginTime),Orders::getCreateTime,beginTime)
                    .lt(StringUtils.isNotEmpty(endTime),Orders::getCreateTime,endTime);
            List<Orders> list = ordersService.list(queryWrapper);
            //分页
            List<?> result = Constants.page(list, pageSize, pageIndex );
            if (result != null) {
                operationResult.setMessage("查询成功");
                operationResult.setData(JSON.parseArray(JSON.toJSONString(result)));
                operationResult.setTotal(list.size());
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("未查到数据");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        }catch (Exception e){
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("查询失败,出现异常: " + e.getMessage());
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }




    @GetMapping("/queryAllOrders")
    public OperationResult<JSONArray> queryAllOrders(HttpSession session, @RequestParam(required = false) Integer pageIndex,
                                                     @RequestParam(required = false) Integer pageSize,
                                                     @RequestParam(required = false) String orderId,
                                                     @RequestParam(required = false) String viewName,
                                                     @RequestParam(required = false) String beginTime,
                                                     @RequestParam(required = false) String endTime){

        log.info("进入查询所有订单方法");
        OperationResult<JSONArray> operationResult = new OperationResult<>();
        log.info("orderId :  "  +orderId  );
        log.info("viewName :  "  +viewName  );
        try {
            String administratorId = (String) session.getAttribute("administrator");
            if (administratorId == null){
                operationResult.setMessage("请使用管理员账号登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
            //构造条件查询对象
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

            //添加查询条件  动态sql
            //使用了范围查询的动态SQL
            queryWrapper.eq(StringUtils.isNotEmpty(orderId),Orders::getOrderId,orderId)
                    .eq(StringUtils.isNotEmpty(viewName),Orders::getViewName,viewName)
                    .gt(StringUtils.isNotEmpty(beginTime),Orders::getCreateTime,beginTime)
                    .lt(StringUtils.isNotEmpty(endTime),Orders::getCreateTime,endTime);
            List<Orders> list = ordersService.list(queryWrapper);
            //分页
            List<?> result = Constants.page(list, pageSize, pageIndex );
            log.info(result.toString());
            if (result != null) {
                operationResult.setMessage("查询成功");
                operationResult.setData(JSON.parseArray(JSON.toJSONString(result)));
                operationResult.setTotal(list.size());
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("未查到数据");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        }catch (Exception e){
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("查询失败,出现异常: " + e.getMessage());
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;


    }





}
