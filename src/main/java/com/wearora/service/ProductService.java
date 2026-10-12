package com.wearora.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wearora.dto.ProductImageResponseDTO;
import com.wearora.dto.ProductPageResponse;
import com.wearora.dto.ProductRequestDTO;
import com.wearora.dto.ProductResponseDTO;
import com.wearora.dto.ProductVariantResponseDTO;
import com.wearora.entity.Category;
import com.wearora.entity.Product;
import com.wearora.exception.CategoryNotFoundException;
import com.wearora.exception.ProductNotFoundException;
import com.wearora.repository.CategoryRepository;
import com.wearora.repository.ProductImageRepository;
import com.wearora.repository.ProductRepository;
import com.wearora.repository.ProductVariantRepository;
import com.wearora.specification.ProductSpecification;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	private final ProductImageRepository productImageRepository;
	private final ProductVariantRepository productVariantRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
			ProductImageRepository productImageRepository, ProductVariantRepository productVariantRepository) {

		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.productImageRepository = productImageRepository;
		this.productVariantRepository = productVariantRepository;
	}

// Searching Logic
	public List<ProductResponseDTO> getAllProducts() {
		return productRepository.findAll().stream().map(this::convertToDTO).toList();
	}

	public ProductPageResponse getProducts(String search, String category, BigDecimal minPrice, BigDecimal maxPrice,
			int page, int size, String sort) {

		if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {

			throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
		}

		if (page < 0) {
			throw new IllegalArgumentException("Page number cannot be negative");
		}

		if (size < 1 || size > 100) {
			throw new IllegalArgumentException("Page size must be between 1 and 100");
		}

		Specification<Product> spec = null;

		if (search != null && !search.isBlank()) {
			spec = ProductSpecification.hasSearch(search);
		}

		if (category != null && !category.isBlank()) {
			Specification<Product> categorySpec = ProductSpecification.hasCategory(category);
			spec = (spec == null) ? categorySpec : spec.and(categorySpec);
		}

		if (minPrice != null) {
			Specification<Product> minPriceSpec = ProductSpecification.hasMinPrice(minPrice);

			spec = (spec == null) ? minPriceSpec : spec.and(minPriceSpec);
		}

		if (maxPrice != null) {
			Specification<Product> maxPriceSpec = ProductSpecification.hasMaxPrice(maxPrice);

			spec = (spec == null) ? maxPriceSpec : spec.and(maxPriceSpec);
		}

		String[] sortParams = sort.split(",");

		if (sortParams.length != 2) {
			throw new IllegalArgumentException("Sort format must be field,direction");
		}

		String sortField = sortParams[0];
		String sortDirection = sortParams[1];

		if (!List.of("price", "productName", "createdAt").contains(sortField)) {
			throw new IllegalArgumentException("Invalid sort field");
		}

		Sort sortOrder;

		if (sortDirection.equalsIgnoreCase("asc")) {
			sortOrder = Sort.by(sortField).ascending();
		} else if (sortDirection.equalsIgnoreCase("desc")) {
			sortOrder = Sort.by(sortField).descending();
		} else {
			throw new IllegalArgumentException("Sort direction must be asc or desc");
		}

//Pagination
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<Product> products = (spec == null) ? productRepository.findAll(pageable)
				: productRepository.findAll(spec, pageable);

		return new ProductPageResponse(products.map(this::convertToDTO).getContent(), products.getNumber(),
				products.getSize(), products.getTotalElements(), products.getTotalPages(), products.isFirst(),
				products.isLast());
	}

	public ProductResponseDTO getProductById(Integer id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
		return convertToDTO(product);
	}

	@Transactional
	public ProductResponseDTO createProduct(ProductRequestDTO request) {
		Product product = new Product();
		applyRequest(product, request);
		Product savedProduct = productRepository.save(product);
		return convertToDTO(savedProduct);
	}

	@Transactional
	public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO request) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

		applyRequest(product, request);

		Product savedProduct = productRepository.save(product);
		return convertToDTO(savedProduct);
	}

	@Transactional
	public void deleteProduct(Integer id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

		productRepository.delete(product);
	}

	private void applyRequest(Product product, ProductRequestDTO request) {
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new CategoryNotFoundException("Category not found: " + request.getCategoryId()));

		if (request.getDiscountPrice() != null && request.getDiscountPrice().compareTo(request.getPrice()) > 0) {
			throw new IllegalArgumentException("Discount price cannot exceed regular price");
		}

		product.setProductName(request.getProductName());
		product.setDescription(request.getDescription());
		product.setMaterial(request.getMaterial());
		product.setPrice(request.getPrice());
		product.setDiscountPrice(request.getDiscountPrice());
		product.setBrand(request.getBrand());
		product.setCategory(category);
	}

	private ProductResponseDTO convertToDTO(Product product) {

		Integer productId = product.getId();

		List<ProductImageResponseDTO> images = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId)
				.stream().map(image -> new ProductImageResponseDTO(image.getId(), image.getImageUrl(),
						image.isPrimary(), image.getDisplayOrder()))
				.toList();

		List<ProductVariantResponseDTO> variants = productVariantRepository.findByProduct_Id(productId).stream()
				.map(variant -> new ProductVariantResponseDTO(variant.getId(), variant.getSize(),
						variant.getStockQuantity()))
				.toList();

		return new ProductResponseDTO(product.getId(), product.getProductName(), product.getDescription(),
				product.getMaterial(), product.getPrice(), product.getDiscountPrice(), product.getBrand(),
				product.getCategory().getId(), product.getCategory().getCategoryName(), images, variants);
	}

}