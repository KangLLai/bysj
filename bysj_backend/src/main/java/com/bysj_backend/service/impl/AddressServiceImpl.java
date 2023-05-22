package com.bysj_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bysj_backend.entity.Address;
import com.bysj_backend.entity.User;
import com.bysj_backend.mapper.AddressMapper;
import com.bysj_backend.mapper.UserMapper;
import com.bysj_backend.service.AddressService;
import com.bysj_backend.service.UserService;
import org.springframework.stereotype.Service;



@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

}
