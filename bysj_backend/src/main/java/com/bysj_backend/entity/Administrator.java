package com.bysj_backend.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 管理员实体类
 */
public class Administrator implements Serializable {

    private String id;
    //管理员名称
    private String name;
    //登录名称
    private String loginName;
    //密码
    private String password;
    //性别 0 女 1 男
    private String sex;
    //手机号码
    private String phone;
    //身份证
    private String idCard;

    //创建时间
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;

    //更新时间
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;
}
