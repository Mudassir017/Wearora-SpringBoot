
package com.wearora.entity;

import java.time.LocalDateTime;

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
@Table(name = "wishlist_items")
public class WishlistItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wishlistItemId")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wishlistId", nullable = false)
	private Wishlist wishlist;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productId", nullable = false)
	private Product product;

	@Column(name = "addedAt", insertable = false, updatable = false)
	private LocalDateTime addedAt;

	public WishlistItem() {
	}

	public Integer getId() {
		return id;
	}

	public Wishlist getWishlist() {
		return wishlist;
	}

	public Product getProduct() {
		return product;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setWishlist(Wishlist wishlist) {
		this.wishlist = wishlist;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
