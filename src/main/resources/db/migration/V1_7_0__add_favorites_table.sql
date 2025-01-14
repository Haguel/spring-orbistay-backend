CREATE TABLE favorites (
       id BIGSERIAL PRIMARY KEY,
       app_user_id BIGINT NOT NULL,
       hotel_id BIGINT NOT NULL,
       FOREIGN KEY (app_user_id) REFERENCES app_user(id),
       FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);