
package com.wearora.dto;

import java.math.BigDecimal;

public class CartItemResponseDTO {
	private Integer cartItemId;
	private Integer variantId;
	private Integer productId;
	private String productName;
	private String size;
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal subtotal;

	public CartItemResponseDTO(Integer cartItemId, Integer variantId, Integer productId, String productName,
			String size, Integer quantity, BigDecimal price) {
		this.cartItemId = cartItemId;
		this.variantId = variantId;
		this.productId = productId;
		this.productName = productName;
		this.size = size;
		this.quantity = quantity;
		this.price = price;
		this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
	}

	public Integer getCartItemId() {
		return cartItemId;
	}

	public Integer getVariantId() {
		return variantId;
	}

	public Integer getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public String getSize() {
		return size;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}
}
