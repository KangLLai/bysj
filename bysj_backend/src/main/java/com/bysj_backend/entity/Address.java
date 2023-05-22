package com.bysj_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {

    //地址id
    private String id;
    //收货人id
    private String userId;
    //收货人
    private String name;
    //收货手机
    private String phone;
    //收获地址
    private String destination;
    //是否为默认地址
    private String isDefault;



}
