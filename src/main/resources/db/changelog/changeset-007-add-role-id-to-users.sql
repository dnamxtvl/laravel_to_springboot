--liquibase formatted sql
--changeset springboot:4

-- 1. Thêm cột role_id vào sau remember_token
ALTER TABLE users
ADD COLUMN role_id INT AFTER remember_token;

--rollback ALTER TABLE users DROP FOREIGN KEY fk_user_role;
--rollback ALTER TABLE users DROP COLUMN role_id;