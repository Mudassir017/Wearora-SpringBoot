
package com.wearora.dto;

import java.util.List;

public class ProductPageResponse {

	private List<ProductResponseDTO> products;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean first;
	private boolean last;

	public ProductPageResponse(List<ProductResponseDTO> products, int currentPage, int pageSize, long totalElements,
			int totalPages, boolean first, boolean last) {

		this.products = products;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.first = first;
		this.last = last;
	}

	public List<ProductResponseDTO> getProducts() {
		return products;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public boolean isFirst() {
		return first;
	}

	public boolean isLast() {
		return last;
	}
}
