ALTER TABLE hotel_room
    ADD COLUMN metering NUMERIC(10, 2);

UPDATE hotel_room
    SET metering = ROUND((10 + random() * 50)::numeric, 2);

ALTER TABLE hotel_room
    ALTER COLUMN metering SET NOT NULL;
