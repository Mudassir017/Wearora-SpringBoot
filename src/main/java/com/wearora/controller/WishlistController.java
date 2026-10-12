package com.wearora.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wearora.dto.WishlistItemResponseDTO;
import com.wearora.service.WishlistService;

import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/wishlist")
@Validated
public class WishlistController {

	private final WishlistService wishlistService;

	public WishlistController(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	@GetMapping
	public List<WishlistItemResponseDTO> getWishlist(Principal principal) {

		return wishlistService.getWishlist(principal.getName());
	}

	@PostMapping("/items")
	public List<WishlistItemResponseDTO> addProduct(Principal principal, @RequestParam @Min(1) Integer productId) {

		return wishlistService.addProduct(principal.getName(), productId);
	}

	@DeleteMapping("/items/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeProduct(Principal principal, @PathVariable @Min(1) Integer itemId) {

		wishlistService.removeProduct(principal.getName(), itemId);
	}
}