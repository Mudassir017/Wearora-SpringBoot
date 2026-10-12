package com.wearora.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "categoryId")
	private Integer id;

	@Column(name = "categoryName", nullable = false, unique = true, length = 100)
	private String categoryName;

	@Column(length = 500)
	private String description;

	@Column(length = 500)
	private String image;

	@Column(name = "createdAt", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	// Constructors

	public Category() {
	}

	public Category(String categoryName, String description, String image) {
		this.categoryName = categoryName;
		this.description = description;
		this.image = image;
	}

	// Getters and Setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}