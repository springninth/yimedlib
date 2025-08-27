-- 创建类目表
CREATE TABLE IF NOT EXISTS category (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(50) NOT NULL UNIQUE
    );

-- 创建用户表
-- 创建用户表
CREATE TABLE IF NOT EXISTS "user" (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
-- 创建古籍表
CREATE TABLE IF NOT EXISTS book (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    category_id BIGINT NOT NULL,
                                    title VARCHAR(100) NOT NULL,
    edition VARCHAR(50),
    author VARCHAR(50),
    location VARCHAR(100),
    dialect_type VARCHAR(50),
    carrier_form VARCHAR(50),
    description VARCHAR(2000),
    code VARCHAR(50),
    volume_count INTEGER,
    page_count INTEGER,
    discoverer VARCHAR(50),
    investigator VARCHAR(50),
    "year" VARCHAR(20),
    copyist VARCHAR(50),
    translator VARCHAR(50),
    source_place VARCHAR(100),
    dialect VARCHAR(50),
    book_type VARCHAR(50),
    binding_form VARCHAR(50),
    page VARCHAR(50),
    paper VARCHAR(50),
    line_style VARCHAR(50),
    damage_status VARCHAR(100),
    translation_status VARCHAR(100),
    FOREIGN KEY (category_id) REFERENCES category(id)
    );

-- 创建图片表
CREATE TABLE IF NOT EXISTS book_image (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          book_id BIGINT NOT NULL,
                                          image_url VARCHAR(255) NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    img_order INTEGER,
    FOREIGN KEY (book_id) REFERENCES book(id)
    );
