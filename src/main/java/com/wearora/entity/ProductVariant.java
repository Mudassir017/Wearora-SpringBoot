
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
@Table(name = "product_variants")
public class ProductVariant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "variantId")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productId", nullable = false)
	private Product product;

	@Column(name = "size", nullable = false, length = 20)
	private String size;

	@Column(name = "stockQuantity", nullable = false)
	private Integer stockQuantity = 0;

	public ProductVariant() {
	}

	public Integer getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public String getSize() {
		return size;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
}
