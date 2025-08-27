package com.example.ymedlib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;

    @Column(nullable = false, length = 100)
    private String title;

    private String edition;
    private String author;
    private String location;
    private String dialectType;
    private String carrierForm;

    // 移除 @Lob，使用 VARCHAR
    @Column(length = 2000)
    private String description;

    private String code;
    private Integer volumeCount;
    private Integer pageCount;
    private String discoverer;
    private String investigator;

    // 转义 year 关键字
    @Column(name = "\"year\"")
    private String year;

    private String copyist;
    private String translator;
    private String sourcePlace;
    private String dialect;
    private String bookType;
    private String bindingForm;
    private String page;
    private String paper;
    private String lineStyle;
    private String damageStatus;
    private String translationStatus;
}
