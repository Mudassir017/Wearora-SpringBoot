package com.wearora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.wearora.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

	List<Product> findByProductNameContainingIgnoreCase(String search);

	List<Product> findByCategory_CategoryNameIgnoreCase(String category);

}