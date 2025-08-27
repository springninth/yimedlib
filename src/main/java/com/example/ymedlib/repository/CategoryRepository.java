package com.example.ymedlib.repository;

import com.example.ymedlib.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface CategoryRepository extends JpaRepository<Category, Long> {
} 