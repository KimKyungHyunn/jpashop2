package com.example.demo.repository.order.simplequery.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

//화면 관련된 부분은 query페키지에 저장하여 관심사 분리

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
	
	private final EntityManager em;
	
	public List<OrderQueryDto> findOrderQueryDtos() {
		List<OrderQueryDto> result = findOrders();
		result.forEach(o -> {
			List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
			o.setOrderItems(orderItems);
		});
		return result;
	}
	
	public List<OrderQueryDto> findOrders() {
		return em.createQuery(
				"select new jpabook.jpashop.repository.order.simplequery.query.OrderQueryDto("
				+ "o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
				" join o.member m" +
				" join o.delivery d", OrderQueryDto.class)
		.getResultList();
	}
	
	public List<OrderItemQueryDto> findOrderItems(Long orderId) {
		return em.createQuery(
				"select new jpabook.jpashop.repository.order.simplequery.query.OrderItemQueryDto("
				+ "oi.order.id, i.name, oi.orderPrice, oi.count) from OrderItem oi" +
				" join oi.item i" +
				" where oi.order.id = :orderId", OrderItemQueryDto.class)
				.setParameter("orderId", orderId)
				.getResultList();
	}
	
	//위의 n+1 문제 해결 방법
	//2번의 쿼리로 값 추출
	public List<OrderQueryDto> findAllByDto_optimization() {
		//쿼리 1번
		List<OrderQueryDto> result = findOrders();
		
		List<Long> orderIds = result.stream()
				.map(o -> o.getOrderId())
				.collect(Collectors.toList());
		
		//쿼리 2번
		List<OrderItemQueryDto> orderItems = em.createQuery(
				"select new jpabook.jpashop.repository.order.simplequery.query.OrderItemQueryDto("
				+ "oi.order.id, i.name, oi.orderPrice, oi.count) from OrderItem oi" +
				" join oi.item i" +
				" where oi.order.id = :orderIds", OrderItemQueryDto.class)
				.setParameter("orderIds", orderIds)
				.getResultList();
		
		Map<Long, List<OrderItemQueryDto>> orderItemMap =  orderItems.stream()
		.collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
		
		result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
		
		return result;
	}
	
	//join 사용하여 데이터 다 가져오기
	public List<OrderFlatDto> findAllByDto_flat(){
		 return em.createQuery(
				"select new jpabook.jpashop.repository.order.simplequery.query.OrderFlatDto("
				+ "o.id, m.name, o.orderDate, o.status, d.address, i.name, io.orderPrice, oi.count)" +
				" from Order o" +
				" join o.member m" +
				" join o.delivery d" +
				" join o.orderItems oi" +
				" join oi.item i", OrderFlatDto.class)
				.getResultList();
	}
	
}
