INSERT INTO users (
    id, first_name, last_name, email, gender, type_user, password, created_at, updated_at
) VALUES
-- 5 học sinh
(gen_random_uuid(), 'Nguyen', 'An', 'an1@example.com', 1, 0, '123456', now(), now()),
(gen_random_uuid(), 'Tran', 'Binh', 'binh1@example.com', 1, 0, '123456', now(), now()),
(gen_random_uuid(), 'Le', 'Cuong', 'cuong1@example.com', 1, 0, '123456', now(), now()),
(gen_random_uuid(), 'Hoang', 'Dung', 'dung1@example.com', 0, 0, '123456', now(), now()),
(gen_random_uuid(), 'Pham', 'Ha', 'ha1@example.com', 0, 0, '123456', now(), now()),

-- 5 phụ huynh
(gen_random_uuid(), 'Nguyen', 'Lan', 'lan1@example.com', 0, 1, '123456', now(), now()),
(gen_random_uuid(), 'Tran', 'Hung', 'hung1@example.com', 1, 1, '123456', now(), now()),
(gen_random_uuid(), 'Le', 'Hanh', 'hanh1@example.com', 0, 1, '123456', now(), now()),
(gen_random_uuid(), 'Hoang', 'My', 'my1@example.com', 0, 1, '123456', now(), now()),
(gen_random_uuid(), 'Pham', 'Tuan', 'tuan1@example.com', 1, 1, '123456', now(), now()),

-- 5 nhà trường
(gen_random_uuid(), 'Truong', 'ABC', 'school1@example.com', 1, 2, '123456', now(), now()),
(gen_random_uuid(), 'Truong', 'DEF', 'school2@example.com', 1, 2, '123456', now(), now()),
(gen_random_uuid(), 'Truong', 'XYZ', 'school3@example.com', 1, 2, '123456', now(), now()),
(gen_random_uuid(), 'Truong', 'GHI', 'school4@example.com', 1, 2, '123456', now(), now()),
(gen_random_uuid(), 'Truong', 'JKL', 'school5@example.com', 1, 2, '123456', now(), now()),

-- 5 admin
(gen_random_uuid(), 'Admin', 'One', 'admin1@example.com', 1, 3, '123456', now(), now()),
(gen_random_uuid(), 'Admin', 'Two', 'admin2@example.com', 1, 3, '123456', now(), now()),
(gen_random_uuid(), 'Admin', 'Three', 'admin3@example.com', 1, 3, '123456', now(), now()),
(gen_random_uuid(), 'Admin', 'Four', 'admin4@example.com', 1, 3, '123456', now(), now()),
(gen_random_uuid(), 'Admin', 'Five', 'admin5@example.com', 1, 3, '123456', now(), now());
