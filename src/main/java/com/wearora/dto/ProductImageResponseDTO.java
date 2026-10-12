
package com.wearora.dto;

public class ProductImageResponseDTO {

	private Integer id;
	private String imageUrl;
	private boolean primary;
	private Integer displayOrder;

	public ProductImageResponseDTO() {
	}

	public ProductImageResponseDTO(Integer id, String imageUrl, boolean primary, Integer displayOrder) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.primary = primary;
		this.displayOrder = displayOrder;
	}

	public Integer getId() {
		return id;
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
}
