package com.example.ymedlib.repository;

import com.example.ymedlib.model.Book;
import com.example.ymedlib.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByCategory(Category category, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE (:keyword IS NULL OR " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.edition) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.dialectType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.carrierForm) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.discoverer) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.investigator) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.year) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.copyist) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.translator) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.sourcePlace) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.dialect) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.bookType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.bindingForm) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.page) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.paper) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.lineStyle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.damageStatus) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.translationStatus) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 添加按分类统计的方法
    Long countByCategory(Category category);

    // 添加按分类名称统计的方法
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category.name = :categoryName")
    Long countByCategoryName(@Param("categoryName") String categoryName);
}