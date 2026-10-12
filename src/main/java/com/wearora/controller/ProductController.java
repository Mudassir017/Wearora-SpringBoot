package com.wearora.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wearora.dto.ProductPageResponse;
import com.wearora.dto.ProductRequestDTO;
import com.wearora.dto.ProductResponseDTO;
import com.wearora.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ProductPageResponse getAllProducts(@RequestParam(required = false) String search,
			@RequestParam(required = false) String category, @RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "Page cannot be negative") int page,

			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int size,
			@RequestParam(defaultValue = "price,asc") String sort) {

		return productService.getProducts(search, category, minPrice, maxPrice, page, size, sort);
	}

	@GetMapping("/{id}")
	public ProductResponseDTO getProductById(@PathVariable Integer id) {
		return productService.getProductById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO request) {
		return productService.createProduct(request);
	}

	@PutMapping("/{id}")
	public ProductResponseDTO updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductRequestDTO request) {
		return productService.updateProduct(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable Integer id) {
		productService.deleteProduct(id);
	}

}