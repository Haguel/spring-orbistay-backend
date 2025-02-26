ALTER TABLE hotel
    ADD COLUMN check_in_time TIME,
    ADD COLUMN check_out_time TIME;

UPDATE hotel
SET check_in_time = '14:00:00',
    check_out_time = '12:00:00';

ALTER TABLE hotel
    ALTER COLUMN check_in_time SET NOT NULL,
    ALTER COLUMN check_out_time SET NOT NULL;

ALTER TABLE hotel_room
    DROP COLUMN check_in_time,
    DROP COLUMN check_out_time;