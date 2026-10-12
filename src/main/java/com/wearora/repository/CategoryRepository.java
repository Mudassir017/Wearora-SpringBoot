package com.wearora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wearora.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}