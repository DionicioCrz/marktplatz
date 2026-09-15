CREATE TABLE refresh_token
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES app_user (id),
    token_hash  VARCHAR(255) NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    NOT NULL
);