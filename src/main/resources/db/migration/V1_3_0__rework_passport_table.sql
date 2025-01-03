ALTER TABLE passport
    DROP COLUMN date_of_birth,
    DROP COLUMN date_of_issue,
    DROP COLUMN holder_full_name,
    DROP COLUMN nationality,
    DROP COLUMN place_of_birth_id,
    DROP COLUMN gender,
    ADD COLUMN first_name VARCHAR(255) NOT NULL,
    ADD COLUMN last_name VARCHAR(255) NOT NULL,
    ADD COLUMN passport_number VARCHAR(255) NOT NULL