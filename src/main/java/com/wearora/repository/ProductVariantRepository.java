
package com.wearora.repository;

import java.util.List;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

	List<ProductVariant> findByProduct_Id(Integer productId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<ProductVariant> findByIdInOrderByIdAsc(List<Integer> ids);
}
