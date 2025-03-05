-- Create the booking_cancel_rule table
CREATE TABLE booking_cancel_rule
(
    id BIGSERIAL PRIMARY KEY,
    appeal_if_cancelled_before_hours INT NOT NULL,
    cancel_fee DECIMAL(10, 2) NOT NULL,
    hotel_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

-- Add the foreign key column to the hotel table
ALTER TABLE hotel
    ADD COLUMN booking_cancel_rule_id BIGINT,
    ADD FOREIGN KEY (booking_cancel_rule_id) REFERENCES booking_cancel_rule(id);

-- Insert cancellation rules for every second hotel (even hotel IDs) and update the hotel table in one go
WITH cancel_rules AS (
    INSERT INTO booking_cancel_rule (appeal_if_cancelled_before_hours, cancel_fee, hotel_id)
        SELECT 24, 50.00, id
        FROM hotel
        WHERE id % 2 = 0
        RETURNING id AS cancel_rule_id, hotel_id
)
UPDATE hotel h
SET booking_cancel_rule_id = cr.cancel_rule_id
FROM cancel_rules cr
WHERE h.id = cr.hotel_id;