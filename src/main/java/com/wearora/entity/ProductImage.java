
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
@Table(name = "product_images")
public class ProductImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "imageId")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productId", nullable = false)
	private Product product;

	@Column(name = "imageUrl", nullable = false, length = 500)
	private String imageUrl;

	@Column(name = "isPrimary", nullable = false)
	private boolean primary;

	@Column(name = "displayOrder", nullable = false)
	private Integer displayOrder = 0;

	public ProductImage() {
	}

	public Integer getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public boolean isPrimary() {
		return primary;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
