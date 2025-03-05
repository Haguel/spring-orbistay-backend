CREATE TABLE payment_status (
                                id BIGSERIAL PRIMARY KEY,
                                status VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO payment_status (status) VALUES
                                        ('COMPLETED'),
                                        ('ON_CHECK_IN'),
                                        ('REFUNDED');

CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,
                         amount NUMERIC(10, 2) NOT NULL,
                         currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                         payment_method VARCHAR(50) NOT NULL,
                         transaction_id VARCHAR(255) UNIQUE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         payment_status_id BIGINT NOT NULL,
                         booking_id BIGINT NOT NULL,
                         FOREIGN KEY (booking_id) REFERENCES booking(id),
                         FOREIGN KEY (payment_status_id) REFERENCES payment_status(id)
);

ALTER TABLE booking
    ADD COLUMN payment_id BIGINT,
    ADD FOREIGN KEY (payment_id) REFERENCES payment(id);

-- Add "CASH" payment for all existing bookings
INSERT INTO payment (amount, currency, payment_method, payment_status_id, booking_id)
SELECT 0, 'USD', 'CASH', (SELECT id FROM payment_status WHERE status = 'COMPLETED'), id
FROM booking;

CREATE TABLE bank_card (
        id BIGSERIAL PRIMARY KEY,
        card_number VARCHAR(16) NOT NULL UNIQUE,
        card_holder_name VARCHAR(255) NOT NULL,
        expiration_date VARCHAR(5) NOT NULL,
        cvv VARCHAR(3) NOT NULL,
        app_user_id BIGINT NOT NULL,
        FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);

CREATE TABLE booking_payment_option
(
    id                BIGSERIAL PRIMARY KEY,
    option VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO booking_payment_option (option) VALUES
                                        ('CARD'),
                                        ('CASH');

CREATE TABLE hotel_booking_payment_option_link (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    booking_payment_option_id BIGINT NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES hotel(id),
    FOREIGN KEY (booking_payment_option_id) REFERENCES booking_payment_option(id)
);

-- Add "CARD" as a payment option for each hotel
INSERT INTO hotel_booking_payment_option_link (hotel_id, booking_payment_option_id)
SELECT id, (SELECT id FROM booking_payment_option WHERE option = 'CARD')
FROM hotel;

-- Add "CASH" as a payment option for every second hotel
INSERT INTO hotel_booking_payment_option_link (hotel_id, booking_payment_option_id)
SELECT id, (SELECT id FROM booking_payment_option WHERE option = 'CASH')
FROM hotel
WHERE id % 2 = 0;

INSERT INTO booking_status (status) VALUES
                                        ('PENDING');

