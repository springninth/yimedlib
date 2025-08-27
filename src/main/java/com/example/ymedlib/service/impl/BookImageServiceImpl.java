package com.example.ymedlib.service.impl;

import com.example.ymedlib.model.BookImage;
import com.example.ymedlib.model.Book;
import com.example.ymedlib.repository.BookImageRepository;
import com.example.ymedlib.service.BookImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookImageServiceImpl implements BookImageService {
    @Autowired
    private BookImageRepository bookImageRepository;

    @Override
    public BookImage saveBookImage(BookImage bookImage) {
        return bookImageRepository.save(bookImage);
    }

    @Override
    public void deleteBookImage(Long id) {
        bookImageRepository.deleteById(id);
    }

    @Override
    public Optional<BookImage> findById(Long id) {
        return bookImageRepository.findById(id);
    }

    @Override
    public List<BookImage> findByBook(Book book) {
        return bookImageRepository.findByBook(book);
    }

    @Override
    public List<BookImage> findAll() {
        return bookImageRepository.findAll();
    }

    @Override
    public List<BookImage> findByBookOrderByImgOrderAsc(Book book) {
        return bookImageRepository.findByBookOrderByImgOrderAsc(book);
    }
} 