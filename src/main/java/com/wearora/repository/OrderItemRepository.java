package com.wearora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

	List<OrderItem> findByOrder_Id(Integer orderId);
}