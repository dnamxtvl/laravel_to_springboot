-- liquibase formatted sql
-- changeset gemini:20260109-002 context:dev

CREATE TABLE forgot_password (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    otp CHAR(6) NOT NULL,
    status TINYINT NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_forgot_pwd_user_id ON forgot_password(user_id);