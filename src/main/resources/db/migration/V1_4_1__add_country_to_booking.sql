ALTER TABLE booking
    ADD COLUMN country_id BIGINT NOT NULL,
    ADD FOREIGN KEY (country_id) REFERENCES country(id);