package com.example.ymedlib.service;

import com.example.ymedlib.model.Book;
import com.example.ymedlib.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    void deleteBook(Long id);
    Book updateBook(Book book);
    Optional<Book> findById(Long id);
    Page<Book> findByCategory(Category category, Pageable pageable);
    Page<Book> searchByKeyword(String keyword, Pageable pageable);
    List<Book> findAll();

    // 添加统计方法
    Long countByCategory(Category category);
    Long countByCategoryName(String categoryName);
}