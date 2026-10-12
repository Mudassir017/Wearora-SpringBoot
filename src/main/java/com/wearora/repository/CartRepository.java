package com.wearora.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
	Optional<Cart> findByUserId(Integer userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Cart> findWithLockByUserId(Integer userId);
}
