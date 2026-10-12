package com.wearora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	List<CartItem> findByCart_Id(Integer cartId);

	Optional<CartItem> findByIdAndCart_Id(Integer id, Integer cartId);

	Optional<CartItem> findByCart_IdAndVariant_Id(Integer cartId, Integer variantId);
}