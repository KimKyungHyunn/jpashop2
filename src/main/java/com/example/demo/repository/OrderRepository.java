package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import com.example.demo.domain.Order;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
	
	private final EntityManager em;
	
	public void save(Order order) {
		em.persist(order);
	}
	
	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}
	
	public List<Order> findAll(OrderSearch orderSearch){
		//parameter 2개 무조건 다 받는 경우
		return em.createQuery("select o from Order o join o.member m"
				+ " where o.status = :status"+
				" and m.name like :name", Order.class)
		.setParameter("status", orderSearch.getOrderStatus())
		.setParameter("name", orderSearch.getMemberName())
		.setMaxResults(1000)
		.getResultList();	
		
		/*
		 	파라미터가 있을 수도 없을 수도 있어 동적 쿼리로 작동해야할때 가장
			단순하나 비효율적인 해결 방법은 동적 쿼리문을 분기에 맞게 문자열로 생성하는것
			ex) name 값 있으면 String query += where name = name ~~
		 */
		
	}	
	
	//동적쿼리의 다른 방법 JPA criteria
	 /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
        if (isFirstCondition) {
            jpql += " where";
            isFirstCondition = false;
        } else {
            jpql += " and";
        }
        jpql += " o.status = :status";
    }

    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        if (isFirstCondition) {
            jpql += " where";
            isFirstCondition = false;
        } else {
            jpql += " and";
        }
        jpql += " m.name like :name";
    }

    TypedQuery<Order> query = em.createQuery(jpql, Order.class)
            .setMaxResults(1000);

    if (orderSearch.getOrderStatus() != null) {
        query = query.setParameter("status", orderSearch.getOrderStatus());
    }
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        query = query.setParameter("name", orderSearch.getMemberName());
    }

    return query.getResultList();
}

	public List<Order> findAllWithMemberDelivery() {
		return em.createQuery(
				"select o from Order o"
				+ " join fetch o.member m"
				+ " join fetch o.delivery d", Order.class).getResultList();
	}
	//distinct로 db에서 중복 거르고 entity order 중복도 거름
	//일대 다 관계에서 dinstinct 쓰면 페이징 기능 못씀
	public List<Order> findAllWithItem() {
		return em.createQuery(
				"select distinct o from Order o" +
				" join fetch o.member m" +
				" join fetch o.delivery d" +
				" join fetch o.orderItems oi" +
				" join fetch oi.item i", Order.class)
				.setFirstResult(1)
				.setMaxResults(100)
				.getResultList();
	}

	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery(
				"select o from Order o"
				+ " join fetch o.member m"
				+ " join fetch o.delivery d", Order.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	//jpa는 값 타입이나 엔티티만 반환할 수 있어서 query에서 dto생성자에 entity을 넣어 dto로 변환된채 반환
//	public List<SimpleOrderQueryDto> findOrderDtos() {
//		return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.orderStatus, d.address) " +
//				" from Order o" + 
//				" join o.member m" +
//				" join o.delivery d", SimpleOrderQueryDto.class).getResultList();
//	}
	//화면에 종속된 쿼리문이므로 레파지토리에 위치하면 안되고 따로 클래스를 만들어 저장
}
