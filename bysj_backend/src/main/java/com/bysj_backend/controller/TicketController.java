package com.bysj_backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bysj_backend.entity.Orders;
import com.bysj_backend.entity.Ticket;
import com.bysj_backend.entity.User;
import com.bysj_backend.service.TicketService;
import com.bysj_backend.utils.Constants;
import com.bysj_backend.utils.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/ticket")
public class TicketController {


    @Autowired
    private TicketService ticketService;

    /**
     * 新增票务信息
     *
     * @param ticket
     * @return
     */
    @PostMapping("/saveTicket")
    public OperationResult<String> save(@RequestBody Ticket ticket, HttpSession session) {
        log.info("新增,票务信息: {}", ticket.toString());
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            String administratorId = (String) session.getAttribute("administrator");
            if (administratorId == null) {
                operationResult.setMessage("请先使用管理员账号登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
            if (ticket.getCity().equals("北京") || ticket.getCity().equals("上海")||ticket.getCity().equals("天津")||
                    ticket.getCity().equals("重庆")||ticket.getCity().equals("澳门")||ticket.getCity().equals("香港")){
                ticket.setProvince("无");
            }
            //设置唯一id
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("生成唯一id: " + id);
            ticket.setId(id);
            log.info("ticket: " + ticket);
            boolean checkFlag = ticketService.save(ticket);
            if (checkFlag) {
                operationResult.setMessage("新增票务成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("新增票务失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("新增票务失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    @PostMapping("/updateTicket")
    public OperationResult<String> modify(@RequestBody Ticket ticket, HttpSession session) {
        log.info("修改,票务信息: {}", ticket);
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            String administratorId = (String) session.getAttribute("administrator");
            if (administratorId == null) {
                operationResult.setMessage("请先使用管理员账号登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
            if (ticket.getCity().equals("北京") || ticket.getCity().equals("上海")||ticket.getCity().equals("天津")||
                ticket.getCity().equals("重庆")||ticket.getCity().equals("澳门")||ticket.getCity().equals("香港")){
                ticket.setProvince("无");
            }
            boolean checkFlag = ticketService.updateById(ticket);
            if (checkFlag) {
                operationResult.setMessage("票务信息修改成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("票务信息修改失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("更新票务信息方法出现异常: " + e.getMessage());
            operationResult.setMessage("票务信息修改失败，出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 删除票务信息
     *
     * @param id
     * @return
     */
    @PostMapping("/deleteTicket/{id}")
    public OperationResult<String> delete(@PathVariable String id) {
        log.info("删除票务信息方法,id: " + id);
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            boolean b = ticketService.removeById(id);
            if (b) {
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
     * 查询票务信息(查询条件:具体省份,具体城市,模糊地点)
     *
     * @return
     */
    @GetMapping("/queryTicket")
    public OperationResult<JSONArray> queryAllTicket(@RequestParam(required = false) Integer pageIndex,
                                                     @RequestParam(required = false) Integer pageSize,
                                                     @RequestParam(required = false) String province,
                                                     @RequestParam(required = false) String city,
                                                     @RequestParam(required = false) String actor,
                                                     @RequestParam(required = false) String beginTime,
                                                     @RequestParam(required = false) String endTime) {

        log.info("查询票务信息");
        OperationResult<JSONArray> operationResult = new OperationResult<>();
        try {
            LambdaQueryWrapper<Ticket> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StringUtils.isNotEmpty(province), Ticket::getProvince, province);
            queryWrapper.eq(StringUtils.isNotEmpty(city), Ticket::getCity, city);

            //根据演出人名模糊查询
            if (StringUtils.isNotEmpty(actor)){
                queryWrapper.and(wrapper -> wrapper.like(Ticket::getActor,actor));
            }
            queryWrapper.gt(StringUtils.isNotEmpty(beginTime), Ticket::getActionTime,beginTime)
                    .lt(StringUtils.isNotEmpty(endTime),Ticket::getActionTime,endTime);
            queryWrapper.orderByDesc(Ticket::getUpdateTime);
            List<Ticket> list = ticketService.list(queryWrapper);
            //分页
            List<?> result = Constants.page(list, pageSize, pageIndex);
            if (result != null) {
                operationResult.setMessage("查询成功");
                operationResult.setData(JSON.parseArray(JSON.toJSONString(result)));
                //全部个数
                operationResult.setTotal(list.size());
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("未查到数据");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("查询失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }

        return operationResult;
    }




}
