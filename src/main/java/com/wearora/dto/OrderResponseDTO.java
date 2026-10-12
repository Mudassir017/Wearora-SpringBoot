package com.wearora.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

	private Integer orderId;
	private String deliveryName;
	private String shippingAddress;
	private String deliveryPhone;
	private BigDecimal totalAmount;
	private String status;
	private String paymentMethod;
	private String paymentStatus;
	private LocalDateTime orderDate;
	private List<OrderItemResponseDTO> items;

	public OrderResponseDTO(Integer orderId, String deliveryName, String shippingAddress, String deliveryPhone,
			BigDecimal totalAmount, String status, String paymentMethod, String paymentStatus, LocalDateTime orderDate,
			List<OrderItemResponseDTO> items) {

		this.orderId = orderId;
		this.deliveryName = deliveryName;
		this.shippingAddress = shippingAddress;
		this.deliveryPhone = deliveryPhone;
		this.totalAmount = totalAmount;
		this.status = status;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.orderDate = orderDate;
		this.items = items;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public String getDeliveryName() {
		return deliveryName;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public List<OrderItemResponseDTO> getItems() {
		return items;
	}
}
