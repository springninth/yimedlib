-- 防止重复插入分类数据
INSERT INTO category (name) SELECT '医经' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '医经');
INSERT INTO category (name) SELECT '医理' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '医理');
INSERT INTO category (name) SELECT '诊治' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '诊治');
INSERT INTO category (name) SELECT '本草' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '本草');
INSERT INTO category (name) SELECT '病症用药' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '病症用药');
INSERT INTO category (name) SELECT '调护' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '调护');
INSERT INTO category (name) SELECT '医史' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '医史');
INSERT INTO category (name) SELECT '作祭献药' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '作祭献药');
INSERT INTO category (name) SELECT '医算' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '医算');
INSERT INTO category (name) SELECT '综合' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '综合');

-- 防止重复插入管理员用户（密码是 admin123）
INSERT INTO "user" (username, password, role)
SELECT 'admin', '$2a$10$RVLhNbNGu0ZHgha9XVno4.wLbifM7neqAg0h444qlPtaqU5iQ1BLu', 'ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE username = 'admin');

-- 防止重复插入测试古籍
INSERT INTO book (
    category_id, title, edition, author, location, dialect_type, carrier_form, description,
    code, volume_count, page_count, discoverer, investigator, "year", copyist, translator,
    source_place, dialect, book_type, binding_form, page, paper, line_style, damage_status, translation_status
)
SELECT
    (SELECT id FROM category WHERE name = '医经' LIMIT 1),
    '彝医经典古籍', '清代抄本', '阿黑且', '云南楚雄', '彝语', '纸质', '彝医经典古籍内容简介',
    'YJ001', 3, 120, '李发掘', '王调查', '清光绪年间', '张抄写', '赵翻译',
    '云南大姚', '彝语', '医经', '线装', '120', '竹纸', '纵25横15', '轻微破损', '已整理翻译'
WHERE NOT EXISTS (SELECT 1 FROM book WHERE code = 'YJ001');