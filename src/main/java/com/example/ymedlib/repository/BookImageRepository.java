package com.example.ymedlib.repository;

import com.example.ymedlib.model.BookImage;
import com.example.ymedlib.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
    List<BookImage> findByBook(Book book);
    List<BookImage> findByBookOrderByImgOrderAsc(Book book);
} 