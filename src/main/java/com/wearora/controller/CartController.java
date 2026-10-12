package com.wearora.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wearora.dto.CartResponseDTO;
import com.wearora.service.CartService;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	// Get logged-in user's cart
	@GetMapping
	public CartResponseDTO getCart(Principal principal) {
		return cartService.getCart(principal.getName());
	}

	// Add product variant to cart
	@PostMapping("/items")
	public CartResponseDTO addItem(Principal principal, @RequestParam @Min(1) Integer variantId,
			@RequestParam @Min(1) Integer quantity) {

		return cartService.addItem(principal.getName(), variantId, quantity);
	}

	// Update cart item quantity
	@PutMapping("/items/{itemId}")
	public CartResponseDTO updateQuantity(Principal principal, @PathVariable Integer itemId,
			@RequestParam @Min(1) Integer quantity) {

		return cartService.updateQuantity(principal.getName(), itemId, quantity);
	}

	// Remove item from cart
	@DeleteMapping("/items/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeItem(Principal principal, @PathVariable Integer itemId) {

		cartService.removeItem(principal.getName(), itemId);
	}
}