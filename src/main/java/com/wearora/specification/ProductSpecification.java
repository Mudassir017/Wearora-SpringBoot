package com.wearora.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.wearora.entity.Product;

public class ProductSpecification {

	public static Specification<Product> hasSearch(String search) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")),
				"%" + search.toLowerCase() + "%");
	}

	public static Specification<Product> hasCategory(String category) {

		return (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(criteriaBuilder.lower(root.get("category").get("categoryName")), category.toLowerCase());
	}

	public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
	}

	public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
	}
}