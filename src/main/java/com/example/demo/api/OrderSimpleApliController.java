package com.example.demo.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderSearch;
import com.example.demo.repository.order.simplequery.*;

import lombok.RequiredArgsConstructor;

/**
 * 양방향 연관관계에서 해당 api 호출하면 서로의 엔티티를 조회하여 무한순회 에러 발생
 * xToOne(ManyToOne, OneToOne) 최적화 위해
 * one 쪽에 JsonIgnore로 양방향 연관관계 해결
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApliController {
	
	private final OrderRepository orderRepository;
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;
	//entity list return
	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1(){
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for (Order order : all) {
			order.getMember().getName(); //Lazy 강제초기화
			order.getDelivery().getAddress();
		}
		return all;
	}
	
	//dto list return
	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> orderV2(){
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		
		List<SimpleOrderDto> result = orders.stream()
				.map(o -> new SimpleOrderDto(o))
				.collect(Collectors.toList());
		
		return result;
	}
	
	//fetch join 사용으로 n+1 문제 해결
	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> orderV3(){
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		List<SimpleOrderDto> result = orders.stream()
		.map(o -> new SimpleOrderDto(o))
		.collect(Collectors.toList());
		
		return result;
	}
	
	//v3와 차이는 select 문이 깔끔하게 줄어듦
	@GetMapping("/api/v4/simple-orders")
	public List<SimpleOrderQueryDto> orderV4(){
		return orderSimpleQueryRepository.findOrderDtos();	
	}
	
	/**
	 * v3는 내부 order을 변경 없이 가져와 재사용성이 높음
	 * v4는 화면에 최적화 되있지만 재사용 불가하지만 성능 최적화 더 큼
	 * 
	 */
	
	static class SimpleOrderDto {

		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		
		public SimpleOrderDto(Order order) {
			this.orderId = order.getId();
			this.name = order.getMember().getName();
			this.orderDate = order.getOrderDate();
			this.address = order.getDelivery().getAddress();
	}
}
	
}
