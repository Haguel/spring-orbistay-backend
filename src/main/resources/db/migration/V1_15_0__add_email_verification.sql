CREATE TABLE email_verification (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    app_user_id BIGSERIAL NOT NULL,
    expires_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP + INTERVAL '1 day',
    FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);