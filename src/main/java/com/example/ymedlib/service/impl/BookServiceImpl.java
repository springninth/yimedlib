package com.example.ymedlib.service.impl;

import com.example.ymedlib.model.Book;
import com.example.ymedlib.model.Category;
import com.example.ymedlib.repository.BookRepository;
import com.example.ymedlib.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Page<Book> findByCategory(Category category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Book> searchByKeyword(String keyword, Pageable pageable) {
        return bookRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Long countByCategory(Category category) {
        return bookRepository.countByCategory(category);
    }

    @Override
    public Long countByCategoryName(String categoryName) {
        return bookRepository.countByCategoryName(categoryName);
    }
}