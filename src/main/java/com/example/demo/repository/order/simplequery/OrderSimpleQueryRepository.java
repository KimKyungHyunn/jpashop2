package com.example.demo.repository.order.simplequery;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
	
	private final EntityManager em;
	
	public List<SimpleOrderQueryDto> findOrderDtos() {
		return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.orderStatus, d.address) " +
				" from Order o" + 
				" join o.member m" +
				" join o.delivery d", SimpleOrderQueryDto.class).getResultList();
	}
}
