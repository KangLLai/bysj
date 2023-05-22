package com.bysj_backend.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bysj_backend.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
