CREATE TABLE recently_viewed_hotel (
        id BIGSERIAL PRIMARY KEY,
        app_user_id BIGINT NOT NULL,
        hotel_id BIGINT NOT NULL,
        viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (app_user_id) REFERENCES app_user(id),
        FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);