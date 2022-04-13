package com.example.demo.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Address;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.domain.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderSearch;
import com.example.demo.repository.order.simplequery.query.OrderFlatDto;
import com.example.demo.repository.order.simplequery.query.OrderItemQueryDto;
import com.example.demo.repository.order.simplequery.query.OrderQueryDto;
import com.example.demo.repository.order.simplequery.query.OrderQueryRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
	
	private final OrderRepository orderRepository;
	private final OrderQueryRepository orderQueryRepository;
	
	@GetMapping("/api/v1/orders")
	public List<Order> orderV1(){
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for (Order order : all) {
			order.getMember().getName();
			order.getDelivery().getAddress();
			
			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.stream().forEach(o -> o.getItem().getName());
			
		}
		return all;
	}
	
	@GetMapping("/api/v2/orders")
	public List<OrderDto> orderV2(){
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<OrderDto> collect = orders.stream()
		.map(o -> new OrderDto(o))
		.collect(Collectors.toList());
		
		return collect;
	}
	
	@GetMapping("/api/v3/orders")
	public List<OrderDto> orderV3(){
		List<Order> orders = orderRepository.findAllWithItem();
		List<OrderDto> collect = orders.stream()
				.map(o -> new OrderDto(o))
				.collect(Collectors.toList());
				
		return collect;
	}
	
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> orderV3_page(
			@RequestParam(value="offset", defaultValue = "0") int offset,
			@RequestParam(value="limit", defaultValue = "100") int limit){
		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
		
		List<OrderDto> collect = orders.stream()
				.map(o -> new OrderDto(o))
				.collect(Collectors.toList());
				
		return collect;
	}
	
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> orderV4() {	
		return orderQueryRepository.findOrderQueryDtos();
	}
	
	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> orderV5() {
		return orderQueryRepository.findAllByDto_optimization();
	}

//    @GetMapping("/api/v6/orders")
//    public List<OrderQueryDto> ordersV6() {
//        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
//
//        return flats.stream()
//                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
//                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
//                )).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
//                .collect(Collectors.toList());
//    }
	
	@Data
	static class OrderDto{
		
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		//dto 내부에 entity을 갖고 있으면 안됨
		//private List<OrderItem> orderItems;
		private List<OrderItemDto> orderItems;
		
		public OrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			address = order.getDelivery().getAddress();
//			order.getOrderItems().stream().forEach(o-> o.getItem().getName());
//			orderItems = order.getOrderItems();
			orderItems = order.getOrderItems().stream()
					.map(orderItem -> new OrderItemDto(orderItem))
					.collect(Collectors.toList());
		}
	}
	
	@Data
	static class OrderItemDto{
		//item 정보를 모두 보여줄 필요 없으므로 보여줄 값만 세팅
		private String itemName;
		private int orderPrice;
		private int count;
		
		public OrderItemDto(OrderItem orderItem) {
			itemName = orderItem.getItem().getName();
			orderPrice = orderItem.getOrderPrice();
			count = orderItem.getCount();
		}
	}
}
