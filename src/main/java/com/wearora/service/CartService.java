package com.wearora.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wearora.dto.CartItemResponseDTO;
import com.wearora.dto.CartResponseDTO;
import com.wearora.entity.Cart;
import com.wearora.entity.CartItem;
import com.wearora.entity.Product;
import com.wearora.entity.ProductVariant;
import com.wearora.entity.User;
import com.wearora.repository.CartItemRepository;
import com.wearora.repository.CartRepository;
import com.wearora.repository.ProductVariantRepository;
import com.wearora.repository.UserRepository;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductVariantRepository variantRepository;
	private final UserRepository userRepository;

	public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			ProductVariantRepository variantRepository, UserRepository userRepository) {

		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.variantRepository = variantRepository;
		this.userRepository = userRepository;
	}

	// Find the authenticated user using email from JWT
	private User getAuthenticatedUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	// Find existing cart or create a new one
	private Cart getOrCreateCart(Integer userId) {
		return cartRepository.findByUserId(userId).orElseGet(() -> {
			Cart cart = new Cart();
			cart.setUserId(userId);
			return cartRepository.save(cart);
		});
	}

	// Get logged-in user's cart
	@Transactional(readOnly = true)
	public CartResponseDTO getCart(String email) {

		User user = getAuthenticatedUser(email);

		Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);

		if (cart == null) {
			return new CartResponseDTO(null, List.of());
		}

		return buildResponse(cart);
	}

	// Add an item to the logged-in user's cart
	@Transactional
	public CartResponseDTO addItem(String email, Integer variantId, Integer quantity) {

		if (quantity == null || quantity < 1) {
			throw new IllegalArgumentException("Quantity must be at least 1");
		}

		User user = getAuthenticatedUser(email);
		Cart cart = getOrCreateCart(user.getId());

		ProductVariant variant = variantRepository.findById(variantId)
				.orElseThrow(() -> new IllegalArgumentException("Product variant not found: " + variantId));

		if (quantity > variant.getStockQuantity()) {
			throw new IllegalArgumentException("Requested quantity exceeds available stock");
		}

		CartItem item = cartItemRepository.findByCart_IdAndVariant_Id(cart.getId(), variantId).orElseGet(() -> {
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setVariant(variant);
			newItem.setQuantity(0);
			return newItem;
		});

		int newQuantity = item.getQuantity() + quantity;

		if (newQuantity > variant.getStockQuantity()) {
			throw new IllegalArgumentException("Total quantity exceeds available stock");
		}

		item.setQuantity(newQuantity);
		cartItemRepository.save(item);

		return buildResponse(cart);
	}

	// Update item quantity in the logged-in user's cart
	@Transactional
	public CartResponseDTO updateQuantity(String email, Integer itemId, Integer quantity) {

		if (quantity == null || quantity < 1) {
			throw new IllegalArgumentException("Quantity must be at least 1");
		}

		User user = getAuthenticatedUser(email);

		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cart not found"));

		CartItem item = cartItemRepository.findByIdAndCart_Id(itemId, cart.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

		if (quantity > item.getVariant().getStockQuantity()) {
			throw new IllegalArgumentException("Requested quantity exceeds available stock");
		}

		item.setQuantity(quantity);
		cartItemRepository.save(item);

		return buildResponse(cart);
	}

	// Remove item from the logged-in user's cart
	@Transactional
	public void removeItem(String email, Integer itemId) {

		User user = getAuthenticatedUser(email);

		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cart not found"));

		CartItem item = cartItemRepository.findByIdAndCart_Id(itemId, cart.getId())
				.orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

		cartItemRepository.delete(item);
	}

	// Convert cart items into response DTO
	private CartResponseDTO buildResponse(Cart cart) {

		List<CartItemResponseDTO> items = cartItemRepository.findByCart_Id(cart.getId()).stream().map(item -> {
			ProductVariant variant = item.getVariant();
			Product product = variant.getProduct();

			return new CartItemResponseDTO(item.getId(), variant.getId(), product.getId(), product.getProductName(),
					variant.getSize(), item.getQuantity(),
					product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
		}).toList();

		return new CartResponseDTO(cart.getId(), items);
	}
}