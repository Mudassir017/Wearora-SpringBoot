
package com.wearora.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wishlist")
public class Wishlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wishlistId")
	private Integer id;

	@Column(name = "userId", nullable = false)
	private Integer userId;

	@Column(name = "createdAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public Wishlist() {
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

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
