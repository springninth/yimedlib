package com.example.ymedlib.service;

import com.example.ymedlib.model.BookImage;
import com.example.ymedlib.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookImageService {
    BookImage saveBookImage(BookImage bookImage);
    void deleteBookImage(Long id);
    Optional<BookImage> findById(Long id);
    List<BookImage> findByBook(Book book);
    List<BookImage> findAll();
    List<BookImage> findByBookOrderByImgOrderAsc(Book book);
} 