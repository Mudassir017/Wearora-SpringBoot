package com.wearora.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wearora.dto.WishlistItemResponseDTO;
import com.wearora.entity.Product;
import com.wearora.entity.User;
import com.wearora.entity.Wishlist;
import com.wearora.entity.WishlistItem;
import com.wearora.repository.ProductRepository;
import com.wearora.repository.UserRepository;
import com.wearora.repository.WishlistItemRepository;
import com.wearora.repository.WishlistRepository;

@Service
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final WishlistItemRepository itemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public WishlistService(WishlistRepository wishlistRepository, WishlistItemRepository itemRepository,
			ProductRepository productRepository, UserRepository userRepository) {

		this.wishlistRepository = wishlistRepository;
		this.itemRepository = itemRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	private User getAuthenticatedUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	private Wishlist getOrCreateWishlist(Integer userId) {
		return wishlistRepository.findByUserId(userId).orElseGet(() -> {
			Wishlist wishlist = new Wishlist();
			wishlist.setUserId(userId);
			return wishlistRepository.save(wishlist);
		});
	}

	@Transactional(readOnly = true)
	public List<WishlistItemResponseDTO> getWishlist(String email) {

		User user = getAuthenticatedUser(email);

		Wishlist wishlist = wishlistRepository.findByUserId(user.getId()).orElse(null);

		if (wishlist == null) {
			return List.of();
		}

		return itemRepository.findByWishlist_Id(wishlist.getId()).stream().map(this::toDTO).toList();
	}

	@Transactional
	public List<WishlistItemResponseDTO> addProduct(String email, Integer productId) {

		User user = getAuthenticatedUser(email);
		Wishlist wishlist = getOrCreateWishlist(user.getId());

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

		if (!itemRepository.existsByWishlist_IdAndProduct_Id(wishlist.getId(), productId)) {

			WishlistItem item = new WishlistItem();
			item.setWishlist(wishlist);
			item.setProduct(product);
			itemRepository.save(item);
		}

		return itemRepository.findByWishlist_Id(wishlist.getId()).stream().map(this::toDTO).toList();
	}

	@Transactional
	public void removeProduct(String email, Integer itemId) {

		User user = getAuthenticatedUser(email);

		Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

		WishlistItem item = itemRepository.findByIdAndWishlist_Id(itemId, wishlist.getId())
				.orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));

		itemRepository.delete(item);
	}

	private WishlistItemResponseDTO toDTO(WishlistItem item) {
		Product product = item.getProduct();

		return new WishlistItemResponseDTO(item.getId(), product.getId(), product.getProductName(), product.getPrice(),
				product.getDiscountPrice(), item.getAddedAt());
	}
}