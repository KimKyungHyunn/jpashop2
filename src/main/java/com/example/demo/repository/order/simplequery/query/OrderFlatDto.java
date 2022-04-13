package com.example.demo.repository.order.simplequery.query;

import java.time.LocalDateTime;

import com.example.demo.domain.Address;

import lombok.Data;

@Data
public class OrderFlatDto {
	
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private Address address;

	private String itemName;
	private int orderPrice;
	private int count;
	
		
	public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate, Address address, String itemName,
			int orderPrice, int count) {
		super();
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.address = address;
		this.itemName = itemName;
		this.orderPrice = orderPrice;
		this.count = count;
	}
	
}
