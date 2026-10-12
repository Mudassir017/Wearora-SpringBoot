
package com.wearora.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistItemResponseDTO {

	private Integer wishlistItemId;
	private Integer productId;
	private String productName;
	private BigDecimal price;
	private BigDecimal discountPrice;
	private LocalDateTime addedAt;

	public WishlistItemResponseDTO(Integer wishlistItemId, Integer productId, String productName, BigDecimal price,
			BigDecimal discountPrice, LocalDateTime addedAt) {
		this.wishlistItemId = wishlistItemId;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.discountPrice = discountPrice;
		this.addedAt = addedAt;
	}

	public Integer getWishlistItemId() {
		return wishlistItemId;
	}

	public Integer getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}
}
