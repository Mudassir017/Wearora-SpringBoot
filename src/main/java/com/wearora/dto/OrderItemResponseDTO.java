package com.wearora.dto;

import java.math.BigDecimal;

public class OrderItemResponseDTO {

	private Integer orderItemId;
	private Integer variantId;
	private Integer productId;
	private String productName;
	private String size;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal subtotal;

	public OrderItemResponseDTO(Integer orderItemId, Integer variantId, Integer productId, String productName,
			String size, Integer quantity, BigDecimal unitPrice) {

		this.orderItemId = orderItemId;
		this.variantId = variantId;
		this.productId = productId;
		this.productName = productName;
		this.size = size;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
	}

	public Integer getOrderItemId() {
		return orderItemId;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}
}
