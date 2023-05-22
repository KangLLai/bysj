package com.bysj_backend.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders implements Serializable {


    //订单号
    private String orderId;
    //用户id
    private String userId;
    //观演人
    private String viewName;
    //身份证
    private String idCard;
    //票务id
    private String ticketId;
    //标题信息
    private String title;
    //地址id
    private String addressId;
    //收货地址
    private String address;
    //收货人
    private String addressName;
    //手机号
    private String phone;
    //订单状态 1待派送，2已派送，3已完成，4已取消
    private String status;
    //实收金额
    private BigDecimal money;
    //备注
    private String remark;

    //下单时间
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;

    //更新时间
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;


}
