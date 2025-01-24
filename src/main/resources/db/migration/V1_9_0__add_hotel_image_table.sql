CREATE TABLE hotel_image (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES hotel(id)
)