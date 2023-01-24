package com.td.common_service.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.OrderPay;

@Mapper
public interface OrderPayMapper extends BaseMapper<OrderPay> {
}