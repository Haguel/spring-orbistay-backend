CREATE TABLE booking_status (
        id BIGSERIAL PRIMARY KEY,
        status VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO booking_status (status) VALUES
                                        ('ACTIVE'),
                                        ('CHECKED-IN'),
                                        ('CHECKED-OUT'),
                                        ('CANCELED');

ALTER TABLE booking
    ADD COLUMN booking_status_id BIGINT,
    ADD FOREIGN KEY (booking_status_id) REFERENCES booking_status(id);

UPDATE booking
    SET booking_status_id = (SELECT id FROM booking_status WHERE status = 'ACTIVE');

ALTER TABLE booking
    ALTER COLUMN booking_status_id SET NOT NULL;