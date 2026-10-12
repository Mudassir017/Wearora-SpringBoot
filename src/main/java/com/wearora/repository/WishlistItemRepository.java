
package com.wearora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {

	List<WishlistItem> findByWishlist_Id(Integer wishlistId);

	Optional<WishlistItem> findByIdAndWishlist_Id(Integer id, Integer wishlistId);

	boolean existsByWishlist_IdAndProduct_Id(Integer wishlistId, Integer productId);
}
