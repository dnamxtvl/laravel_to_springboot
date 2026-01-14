-- liquibase formatted sql
-- changeset springboot:20240522-01

CREATE TABLE user_refresh_tokens (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(500) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

-- changeset springboot:20240522-02
CREATE UNIQUE INDEX idx_token_unique ON user_refresh_tokens (token);

-- changeset springboot:20240522-03
CREATE INDEX idx_user_id ON user_refresh_tokens (user_id);