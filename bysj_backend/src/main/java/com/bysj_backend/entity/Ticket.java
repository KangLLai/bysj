package com.bysj_backend.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 票务信息实体类
 */
public class Ticket implements Serializable {

    //票 id
    private String id;
    //票标题
    private String title;
    //出演人
    private String actor;
    //描述信息
    private String describeInfo;
    //图片
    private String image;
    //价格
    private BigDecimal price;
    //省份
    private String province;
    //城市
    private String city;
    //演出地点
    private String place;

    //演出时间
    @JsonFormat(timezone = "GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionTime;

    //创建时间

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;




}
