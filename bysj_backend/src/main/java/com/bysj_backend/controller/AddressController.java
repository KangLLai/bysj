package com.bysj_backend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bysj_backend.entity.Address;
import com.bysj_backend.entity.Administrator;
import com.bysj_backend.service.AddressService;
import com.bysj_backend.utils.Constants;
import com.bysj_backend.utils.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 新增收货信息
     *
     * @param address
     * @return
     */
    @PostMapping("/saveAddress")
    public OperationResult<String> save(@RequestBody Address address, HttpSession session) {
        log.info("新增,收货地址信息: {}", address.toString());
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            //设置唯一id
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            log.info("生成唯一id: " + id);
            address.setId(id);

            String userId = (String) session.getAttribute("user");
            log.info("当前的userId为: " + userId);
            if (null == userId) {
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            } else {
                address.setUserId(userId);
            }

            if(Constants.IS_DEFAULT_TRUE.equals(address.getIsDefault())){
                LambdaUpdateWrapper<Address> wrapper1 = new LambdaUpdateWrapper<>();
                wrapper1.eq(Address::getUserId, userId);
                wrapper1.set(Address::getIsDefault, Constants.IS_DEFAULT_FALSE);
                //SQL:update address set is_default = 0 where user_id = ?
                addressService.update(wrapper1);
            }


            log.info("address: " + address);
            boolean save = addressService.save(address);
            if (save) {
                operationResult.setMessage("新增收货地址成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("新增收货地址失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("新增收货地址失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 修改收货信息
     *
     * @param address
     * @return
     */
    @PostMapping("/updateAddress")
    public OperationResult<String> modify(@RequestBody Address address) {
        log.info("修改,收货地址信息: {}", address.toString());
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            boolean b = addressService.updateById(address);
            if (b) {
                operationResult.setMessage("修改收货信息成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("修改收货信息失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("修改收货信息失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 删除收货信息
     *
     * @param id
     * @return
     */
    @PostMapping("/deleteAddress/{id}")
    public OperationResult<String> delete(@PathVariable String id) {
        log.info("删除收货信息方法,id: " + id);
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            boolean b = addressService.removeById(id);
            if (b) {
                operationResult.setMessage("删除收货信息成功");
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            } else {
                operationResult.setMessage("删除收货信息失败");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("删除收货信息失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }

        return operationResult;

    }

    /**
     * 设置默认地址
     */
    @PostMapping("/default/{id}")
    public OperationResult<String> setDefaultAddress(@PathVariable String id, HttpSession session) {
        log.info("进入设置默认地址方法");
        log.info("收货信息id:{}", id);
        OperationResult<String> operationResult = new OperationResult<>();
        try {
            String userId = (String) session.getAttribute("user");
            if (userId != null) {
                boolean update = false;
                LambdaUpdateWrapper<Address> wrapper1 = new LambdaUpdateWrapper<>();
                wrapper1.eq(Address::getUserId, userId);
                wrapper1.set(Address::getIsDefault, Constants.IS_DEFAULT_FALSE);
                //SQL:update address set is_default = 0 where user_id = ?
                addressService.update(wrapper1);

                LambdaUpdateWrapper<Address> wrapper2 = new LambdaUpdateWrapper<>();
                wrapper2.eq(Address::getId, id);
                wrapper2.set(Address::getIsDefault, Constants.IS_DEFAULT_TRUE);
                //SQL:update address_book set is_default = 1 where id = ?
                update = addressService.update(wrapper2);
                if (update) {
                    operationResult.setMessage("设置成功");
                    operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                } else {
                    operationResult.setMessage("设置失败");
                    operationResult.setStatus(OperationResult.STATUS_FAILURE);
                }
            } else {
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }
        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("设置默认地址失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }

    /**
     * 查询默认地址
     * @param session
     * @return
     */
    @PostMapping("/getDefault")
    public OperationResult<JSON> getDefault(HttpSession session) {
        log.info("查询默认地址");
        OperationResult<JSON> operationResult = new OperationResult<>();
        try {
            String userId = (String) session.getAttribute("user");
            if (userId != null) {
                LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Address::getUserId, userId);
                queryWrapper.eq(Address::getIsDefault, Constants.IS_DEFAULT_TRUE);
                Address defaultAddress = addressService.getOne(queryWrapper);

                operationResult.setMessage("查询默认地址成功");
                operationResult.setData((JSON) JSON.toJSON(defaultAddress));
                operationResult.setStatus(OperationResult.STATUS_SUCCESS);
            }else {
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }

        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("查询失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


    /**
     * 查询全部收货信息
     *
     * @param session
     * @return
     */
    @PostMapping("/queryAddress")
    public OperationResult<JSONArray> query(HttpSession session) {
        log.info("进入查询所有收货信息方法");
        OperationResult<JSONArray> operationResult = new OperationResult<>();
        List<Address> addressList = null;
        try {
            String userId = (String) session.getAttribute("user");
            if (userId != null) {
                LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Address::getUserId, userId);
                addressList = addressService.list(queryWrapper);
                if (addressList.isEmpty()) {
                    operationResult.setMessage("当前用户未添加收货信息");
                    operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                } else {
                    operationResult.setMessage("查询成功");
                    operationResult.setTotal(addressList.size());
                    operationResult.setData(JSON.parseArray(JSON.toJSONString(addressList)));
                    operationResult.setStatus(OperationResult.STATUS_SUCCESS);
                }
            } else {
                operationResult.setMessage("请先登录");
                operationResult.setStatus(OperationResult.STATUS_FAILURE);
                operationResult.setErrorCode(Constants.NOTLOGIN);
                return operationResult;
            }

        } catch (Exception e) {
            log.info("异常信息: " + e.getMessage());
            operationResult.setMessage("查询收货信息失败,出现异常");
            operationResult.setStatus(OperationResult.STATUS_FAILURE);
        }
        return operationResult;
    }


}
