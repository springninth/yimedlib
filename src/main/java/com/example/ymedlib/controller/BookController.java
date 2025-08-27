package com.example.ymedlib.controller;

import com.example.ymedlib.model.Book;
import com.example.ymedlib.model.Category;
import com.example.ymedlib.service.BookService;
import com.example.ymedlib.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/page")
    public Page<Book> getBooksByPage(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.findAll().isEmpty() ? Page.empty() : bookService.searchByKeyword("", pageable);
    }

    @GetMapping("/{id}")
    public Optional<Book> getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    @PutMapping
    public Book updateBook(@RequestBody Book book) {
        return bookService.updateBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/category/{categoryId}")
    public Page<Book> getBooksByCategory(@PathVariable Long categoryId, @RequestParam int page, @RequestParam int size) {
        Optional<Category> category = categoryService.findById(categoryId);
        Pageable pageable = PageRequest.of(page, size);
        return category.map(value -> bookService.findByCategory(value, pageable)).orElse(Page.empty());
    }

    /**
     * 处理前端的分类查询请求: GET /api/books/category/?page=0&size=10
     */
    @GetMapping("/category/")
    public Page<Book> getBooksByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {

        // 添加日志便于调试
        System.out.println("收到分类查询请求: category=" + category + ", page=" + page + ", size=" + size);

        Pageable pageable = PageRequest.of(page, size);

        if (category != null && !category.isEmpty()) {
            // 如果有分类参数，尝试按分类查询
            try {
                // 方式1：如果你的CategoryService有findByName方法
                // Optional<Category> categoryEntity = categoryService.findByName(category);
                // if (categoryEntity.isPresent()) {
                //     return bookService.findByCategory(categoryEntity.get(), pageable);
                // }

                // 方式2：使用搜索功能（推荐，因为更灵活）
                return bookService.searchByKeyword(category, pageable);

            } catch (Exception e) {
                System.err.println("分类查询出错: " + e.getMessage());
                // 出错时返回所有数据
                return bookService.searchByKeyword("", pageable);
            }
        }

        // 如果没有分类参数，返回所有书籍
        return bookService.searchByKeyword("", pageable);
    }

    @GetMapping("/search")
    public Page<Book> searchBooks(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.searchByKeyword(keyword, pageable);
    }
} 