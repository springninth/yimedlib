package com.example.ymedlib.controller;

import com.example.ymedlib.model.BookImage;
import com.example.ymedlib.model.Book;
import com.example.ymedlib.service.BookImageService;
import com.example.ymedlib.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/book-images")
public class BookImageController {
    @Autowired
    private BookImageService bookImageService;
    @Autowired
    private BookService bookService;

    @GetMapping
    public List<BookImage> getAllBookImages() {
        return bookImageService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<BookImage> getBookImageById(@PathVariable Long id) {
        return bookImageService.findById(id);
    }

    @GetMapping("/book/{bookId}")
    public List<BookImage> getImagesByBook(@PathVariable Long bookId) {
        Optional<Book> book = bookService.findById(bookId);
        return book.map(bookImageService::findByBookOrderByImgOrderAsc).orElse(List.of());
    }

    @PostMapping
    public BookImage createBookImage(@RequestBody BookImage bookImage) {
        return bookImageService.saveBookImage(bookImage);
    }

    @PostMapping("/upload/{bookId}")
    public ResponseEntity<?> uploadBookImages(
            @PathVariable Long bookId,
            @RequestParam(value = "file") MultipartFile[] files,
            @RequestParam(value = "afterId", required = false) Long afterId) {

        try {
            // 验证文件数组是否为null或空
            if (files == null || files.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No files provided");
            }

            Optional<Book> bookOpt = bookService.findById(bookId);
            if (bookOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }

            Book book = bookOpt.get();
            List<BookImage> images = bookImageService.findByBookOrderByImgOrderAsc(book);

            // 安全地获取插入位置
            int insertOrder = images != null ? images.size() : 0;

            if (afterId != null && images != null) {
                for (int i = 0; i < images.size(); i++) {
                    BookImage img = images.get(i);
                    if (img != null && img.getId() != null && img.getId().equals(afterId)) {
                        insertOrder = img.getImgOrder() != null ? img.getImgOrder() + 1 : i + 1;
                        break;
                    }
                }

                // 更新后续图片的顺序
                for (BookImage img : images) {
                    if (img != null && img.getImgOrder() != null && img.getImgOrder() >= insertOrder) {
                        img.setImgOrder(img.getImgOrder() + files.length);
                        bookImageService.saveBookImage(img);
                    }
                }
            }

            // 处理文件上传
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    try {
                        // 验证文件名
                        String originalFilename = file.getOriginalFilename();
                        if (originalFilename == null || originalFilename.trim().isEmpty()) {
                            originalFilename = "unnamed_file";
                        }

                        String filename = System.currentTimeMillis() + "_" + originalFilename;
                        String url = "/uploads/book-images/" + bookId + "/" + filename;

                        // 创建上传目录
                        Path uploadPath = Paths.get("uploads/book-images/" + bookId);
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        // 保存文件
                        Path filePath = uploadPath.resolve(filename);
                        file.transferTo(filePath);

                        // 创建数据库记录
                        BookImage bookImage = new BookImage();
                        bookImage.setBook(book);
                        bookImage.setImageUrl(url);
                        bookImage.setUploadTime(LocalDateTime.now());
                        bookImage.setImgOrder(insertOrder);
                        bookImageService.saveBookImage(bookImage);

                        insertOrder++;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to upload file: " + file.getOriginalFilename() + ". Error: " + e.getMessage());
                    }
                }
            }

            return ResponseEntity.ok("Upload successful");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookImage(@PathVariable Long id) {
        try {
            Optional<BookImage> imgOpt = bookImageService.findById(id);
            if (imgOpt.isPresent()) {
                Book book = imgOpt.get().getBook();
                bookImageService.deleteBookImage(id);

                // 重新排序
                List<BookImage> images = bookImageService.findByBookOrderByImgOrderAsc(book);
                if (images != null) {
                    for (int i = 0; i < images.size(); i++) {
                        BookImage img = images.get(i);
                        if (img != null) {
                            img.setImgOrder(i);
                            bookImageService.saveBookImage(img);
                        }
                    }
                }
                return ResponseEntity.ok("Delete successful");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete failed: " + e.getMessage());
        }
    }
}