package com.mine.hardware_pro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mine.hardware_pro.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
