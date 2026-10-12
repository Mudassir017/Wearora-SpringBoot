package com.wearora.dto;

public class CategoryResponseDTO {

	private Integer id;
	private String categoryName;
	private String description;
	private String image;

	public CategoryResponseDTO(Integer id, String categoryName, String description, String image) {

		this.id = id;
		this.categoryName = categoryName;
		this.description = description;
		this.image = image;
	}

	public Integer getId() {
		return id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}
}