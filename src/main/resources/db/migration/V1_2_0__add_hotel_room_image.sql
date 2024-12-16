CREATE TABLE hotel_room_image (
    id BIGSERIAL PRIMARY KEY,
    hotel_room_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id)
);

DO $$
DECLARE
    hotel_room_record RECORD;
    image_url TEXT;
BEGIN
    FOR hotel_room_record IN SELECT id, unnest(images_url) AS image_url FROM hotel_room
        LOOP
            INSERT INTO hotel_room_image (hotel_room_id, image_url)
            VALUES (hotel_room_record.id, hotel_room_record.image_url);
        END LOOP;
END $$;

ALTER TABLE hotel_room
    DROP COLUMN images_url;