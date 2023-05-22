package com.bysj_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bysj_backend.entity.Administrator;
import com.bysj_backend.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
