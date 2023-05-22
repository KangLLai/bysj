package com.bysj_backend.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bysj_backend.entity.Orders;
import com.bysj_backend.mapper.OrdersMapper;
import com.bysj_backend.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
