package com.example.ymedlib.controller;

import com.example.ymedlib.model.Category;
import com.example.ymedlib.service.CategoryService;
import com.example.ymedlib.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @PutMapping
    public Category updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    /**
     * 获取各个分类的古籍统计数据
     */
    @GetMapping("/stats")
    public Map<String, Long> getCategoryStats() {
        Map<String, Long> stats = new HashMap<>();

        // 预定义的分类列表（与前端保持一致）
        String[] categories = {"医经", "医理", "诊治", "本草", "病症用药", "调护", "医史", "作祭献药", "医算", "综合"};

        for (String category : categories) {
            Long count = bookService.countByCategoryName(category);
            stats.put(category, count != null ? count : 0L);
        }

        return stats;
    }
}