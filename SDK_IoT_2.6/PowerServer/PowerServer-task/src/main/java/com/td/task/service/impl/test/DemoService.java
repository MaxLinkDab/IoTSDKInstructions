package com.td.task.service.impl.test;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.td.task.mapper.test.OrderMapper;
import com.td.task.model.test.Order;

@Service
public class DemoService {

	@Resource
	private OrderMapper orderRepository;

	public void demo() {
		for (int i = 0; i < 10; i++) {
			Order order = new Order();
			order.setOrderId(i < 5 ? i % 2 : i % 2 + 1);
			order.setUserId(i);
			order.setStatus("Tinko");
			orderRepository.insert(order);
		}
		System.out.println("Insert Success");
	}
	public List<Order> get() {
		List<Order> list_data = null;
		
		Order orderQuery = new Order();
		orderQuery.setUserId(2);
		list_data = orderRepository.get(orderQuery);
		list_data.forEach(System.out::println);
		System.out.println("Get Success");
		
		return list_data;
	}
}
