package com.example.demo.repository.order.simplequery.query;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.domain.Address;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "orderId")
public class OrderQueryDto {
	
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private Address address;
	private List<OrderItemQueryDto> orderItems;
	
	public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, Address address,
			List<OrderItemQueryDto> orderItems) {
		super();
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.address = address;
		this.orderItems = orderItems;
	}
	
}
