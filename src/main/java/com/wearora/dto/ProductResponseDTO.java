
package com.wearora.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductResponseDTO {

	private Integer id;
	private String productName;
	private String description;
	private String material;
	private BigDecimal price;
	private BigDecimal discountPrice;
	private String brand;
	private Integer categoryId;
	private String categoryName;

	private List<ProductImageResponseDTO> images = new ArrayList<>();
	private List<ProductVariantResponseDTO> variants = new ArrayList<>();

	public ProductResponseDTO() {
	}

	public ProductResponseDTO(Integer id, String productName, String description, String material, BigDecimal price,
			BigDecimal discountPrice, String brand, Integer categoryId, String categoryName) {

		this.id = id;
		this.productName = productName;
		this.description = description;
		this.material = material;
		this.price = price;
		this.discountPrice = discountPrice;
		this.brand = brand;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	public ProductResponseDTO(Integer id, String productName, String description, String material, BigDecimal price,
			BigDecimal discountPrice, String brand, Integer categoryId, String categoryName,
			List<ProductImageResponseDTO> images, List<ProductVariantResponseDTO> variants) {

		this(id, productName, description, material, price, discountPrice, brand, categoryId, categoryName);

		this.images = images;
		this.variants = variants;
	}

	public Integer getId() {
		return id;
	}

	public String getProductName() {
		return productName;
	}

	public String getDescription() {
		return description;
	}

	public String getMaterial() {
		return material;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}

	public String getBrand() {
		return brand;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public List<ProductImageResponseDTO> getImages() {
		return images;
	}

	public List<ProductVariantResponseDTO> getVariants() {
		return variants;
	}
}
