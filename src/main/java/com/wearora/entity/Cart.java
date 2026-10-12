
package com.wearora.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cartId")
	private Integer id;

	@Column(name = "userId", nullable = false, unique = true)
	private Integer userId;

	@Column(name = "createdAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public Cart() {
	}

	public Integer getId() {
		return id;
	}

	public Integer getUserId() {
		return userId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
