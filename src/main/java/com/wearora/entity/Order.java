package com.wearora.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orderId")
	private Integer id;

	@Column(name = "userId", nullable = false)
	private Integer userId;

	@Column(name = "deliveryName", nullable = false, length = 100)
	private String deliveryName;

	@Column(name = "shippingAddress", nullable = false, length = 1000)
	private String shippingAddress;

	@Column(name = "deliveryPhone", nullable = false, length = 15)
	private String deliveryPhone;

	@Column(name = "totalAmount", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "orderStatus", nullable = false, length = 30)
	private String status = "PLACED";

	@Column(name = "paymentMethod", nullable = false, length = 30)
	private String paymentMethod;

	@Column(name = "paymentStatus", nullable = false, length = 30)
	private String paymentStatus = "PENDING";

	@Column(name = "orderDate", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public Order() {
	}

	public Integer getId() {
		return id;
	}

	public Integer getUserId() {
		return userId;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
}