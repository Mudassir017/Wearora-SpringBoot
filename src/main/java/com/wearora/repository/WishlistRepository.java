
package com.wearora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

	Optional<Wishlist> findByUserId(Integer userId);
}
