package com.td.task.mapper.test;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.td.task.model.test.Order;

@Mapper
public interface OrderMapper {

	Long insert(Order order);
	List<Order> get(Order orderQuery);
}
