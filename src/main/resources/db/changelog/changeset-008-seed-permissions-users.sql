--liquibase formatted sql
--changeset springboot:5

INSERT INTO permissions (name, description) VALUES
('USER_VIEW', 'Cho phép xem danh sách và chi tiết người dùng'),
('USER_CREATE', 'Cho phép tạo mới người dùng'),
('USER_UPDATE', 'Cho phép chỉnh sửa thông tin người dùng'),
('USER_DELETE', 'Cho phép xóa người dùng'),
('ROLE_VIEW', 'Cho phép xem danh sách và chi tiết role'),
('ROLE_CREATE', 'Cho phép tạo mới 1 role'),
('ASSIGN_ROLE', 'Cho phép gán quyền cho người dùng'),
('ROLE_UPDATE', 'Cho phép chỉnh sửa thông tin quyền'),
('ROLE_DELETE', 'Cho phép xóa quyền');

INSERT INTO roles (name, description) VALUES
('ADMIN', 'Role admin cao nhất');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

--rollback DELETE FROM permissions WHERE name IN ('USER_VIEW', 'USER_CREATE', 'USER_UPDATE', 'USER_DELETE');