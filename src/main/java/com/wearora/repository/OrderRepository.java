package com.wearora.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wearora.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByUserIdOrderByIdDesc(Integer userId);

	@Query("""
			SELECT o FROM Order o
			WHERE o.id = :orderId AND o.userId = :userId
			""")
	Optional<Order> findUserOrder(@Param("orderId") Integer orderId, @Param("userId") Integer userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT o FROM Order o
			WHERE o.id = :orderId AND o.userId = :userId
			""")
	Optional<Order> findUserOrderWithLock(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
}
