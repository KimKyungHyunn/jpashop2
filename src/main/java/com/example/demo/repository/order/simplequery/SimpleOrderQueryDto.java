package com.example.demo.repository.order.simplequery;

import java.time.LocalDateTime;
import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderStatus;

import lombok.Data;

@Data
public class SimpleOrderQueryDto {

		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		
		public SimpleOrderQueryDto(Long orderId, String name, LocalDateTime orderDate, 
				OrderStatus orderStatus, Address address) {
			this.orderId = orderId;
			this.name = name;
			this.orderDate = orderDate;
			this.address = address;
	}
}
