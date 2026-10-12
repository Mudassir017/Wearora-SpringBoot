
package com.wearora.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {
	private Integer cartId;
	private List<CartItemResponseDTO> items;
	private BigDecimal total;

	public CartResponseDTO(Integer cartId, List<CartItemResponseDTO> items) {
		this.cartId = cartId;
		this.items = items;
		this.total = items.stream().map(CartItemResponseDTO::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public Integer getCartId() {
		return cartId;
	}

	public List<CartItemResponseDTO> getItems() {
		return items;
	}

	public BigDecimal getTotal() {
		return total;
	}
}
