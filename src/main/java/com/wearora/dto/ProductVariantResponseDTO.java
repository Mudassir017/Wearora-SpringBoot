
package com.wearora.dto;

public class ProductVariantResponseDTO {

	private Integer id;
	private String size;
	private Integer stockQuantity;

	public ProductVariantResponseDTO() {
	}

	public ProductVariantResponseDTO(Integer id, String size, Integer stockQuantity) {
		this.id = id;
		this.size = size;
		this.stockQuantity = stockQuantity;
	}

	public Integer getId() {
		return id;
	}

	public String getSize() {
		return size;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}
}
