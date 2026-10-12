
package com.wearora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cartItemId")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cartId", nullable = false)
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variantId", nullable = false)
	private ProductVariant variant;

	@Column(nullable = false)
	private Integer quantity = 1;

	public CartItem() {
	}

	public Integer getId() {
		return id;
	}

	public Cart getCart() {
		return cart;
	}

	public ProductVariant getVariant() {
		return variant;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public void setVariant(ProductVariant variant) {
		this.variant = variant;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
