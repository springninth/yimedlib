package com.example.ymedlib.service;

import com.example.ymedlib.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    Category updateCategory(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
} 