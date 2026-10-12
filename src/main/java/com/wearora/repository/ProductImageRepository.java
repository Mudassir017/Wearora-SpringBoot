
package com.wearora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

	List<ProductImage> findByProduct_IdOrderByDisplayOrderAsc(Integer productId);
}
