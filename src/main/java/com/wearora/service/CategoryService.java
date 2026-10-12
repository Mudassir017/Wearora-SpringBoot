package com.wearora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.wearora.dto.CategoryResponseDTO;
import com.wearora.entity.Category;
import com.wearora.exception.CategoryNotFoundException;
import com.wearora.repository.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<CategoryResponseDTO> getAllCategories() {
		return categoryRepository.findAll().stream().map(this::convertToDTO).toList();
	}

	public CategoryResponseDTO getCategoryById(@PathVariable Integer id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
		return convertToDTO(category);
	}

	private CategoryResponseDTO convertToDTO(Category category) {

		return new CategoryResponseDTO(category.getId(), category.getCategoryName(), category.getDescription(),
				category.getImage());
	}
}
