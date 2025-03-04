CREATE TABLE country (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         code VARCHAR(10) NOT NULL
);

CREATE TABLE birthplace (
                            id BIGSERIAL PRIMARY KEY,
                            country_id BIGINT NOT NULL,
                            city VARCHAR(255) NOT NULL,
                            FOREIGN KEY (country_id) REFERENCES country(id)
);

CREATE TABLE address (
                         id BIGSERIAL PRIMARY KEY,
                         city VARCHAR(255) NOT NULL,
                         street VARCHAR(255) NOT NULL,
                         country_id BIGINT NOT NULL,
                         FOREIGN KEY (country_id) REFERENCES country(id)
);

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(255) NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          phone VARCHAR(50),
                          email VARCHAR(255) NOT NULL UNIQUE,
                          birth_date DATE,
                          gender VARCHAR(50),
                          avatar_url VARCHAR(255),
                          citizenship_country_id BIGINT,
                          residence_address_id BIGINT,
                          role VARCHAR(50) NOT NULL,
                          FOREIGN KEY (citizenship_country_id) REFERENCES country(id),
                          FOREIGN KEY (residence_address_id) REFERENCES address(id)
);

CREATE TABLE passport (
                          id BIGSERIAL PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          passport_number VARCHAR(255) NOT NULL,
                          expiration_date DATE NOT NULL,
                          issuing_country_id BIGINT NOT NULL,
                          app_user_id BIGINT,
                          FOREIGN KEY (issuing_country_id) REFERENCES country(id),
                          FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);

CREATE TABLE hotel (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       address_id BIGINT NOT NULL,
                       short_desc TEXT NOT NULL,
                       full_desc TEXT NOT NULL,
                       stars INT NOT NULL,
                       main_image_url VARCHAR(255),
                       check_in_time TIME NOT NULL,
                       check_out_time TIME NOT NULL,
                       FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE hotel_image (
                             id BIGSERIAL PRIMARY KEY,
                             hotel_id BIGINT NOT NULL,
                             image_url TEXT NOT NULL,
                             FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE hotel_room (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description TEXT NOT NULL,
                            cost_per_night NUMERIC(10, 2) NOT NULL,
                            area NUMERIC(10, 2) NOT NULL,
                            child_friendly BOOLEAN NOT NULL DEFAULT FALSE,
                            capacity INT NOT NULL,
                            images_url TEXT[],
                            hotel_id BIGINT NOT NULL,
                            FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE hotel_room_image (
                                  id BIGSERIAL PRIMARY KEY,
                                  hotel_room_id BIGINT NOT NULL,
                                  image_url VARCHAR(255) NOT NULL,
                                  FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id)
);

CREATE TABLE facility (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE bed_type (
                          id BIGSERIAL PRIMARY KEY,
                          bed_type VARCHAR(255) NOT NULL
);

CREATE TABLE room_facility_link (
                                    id BIGSERIAL PRIMARY KEY,
                                    hotel_room_id BIGINT NOT NULL,
                                    facility_id BIGINT NOT NULL,
                                    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
                                    FOREIGN KEY (facility_id) REFERENCES facility(id)
);

CREATE TABLE room_bed_link (
                               id BIGSERIAL PRIMARY KEY,
                               hotel_room_id BIGINT NOT NULL,
                               bed_type_id BIGINT NOT NULL,
                               FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
                               FOREIGN KEY (bed_type_id) REFERENCES bed_type(id)
);

CREATE TABLE highlight (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           image_url VARCHAR(255) NOT NULL
);

CREATE TABLE room_highlight_link (
                                     id BIGSERIAL PRIMARY KEY,
                                     hotel_room_id BIGINT NOT NULL,
                                     highlight_id BIGINT NOT NULL,
                                     FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
                                     FOREIGN KEY (highlight_id) REFERENCES highlight(id)
);

CREATE TABLE hotel_highlight (
                                 id BIGSERIAL PRIMARY KEY,
                                 name VARCHAR(255) NOT NULL UNIQUE,
                                 image_url VARCHAR(255) NOT NULL
);

CREATE TABLE hotel_highlight_link (
                                      id BIGSERIAL PRIMARY KEY,
                                      hotel_id BIGINT NOT NULL,
                                      hotel_highlight_id BIGINT NOT NULL,
                                      FOREIGN KEY (hotel_id) REFERENCES hotel(id),
                                      FOREIGN KEY (hotel_highlight_id) REFERENCES hotel_highlight(id)
);

CREATE TABLE booking_status (
                                id BIGSERIAL PRIMARY KEY,
                                status VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE booking (
                         id BIGSERIAL PRIMARY KEY,
                         check_in TIMESTAMP NOT NULL,
                         check_out TIMESTAMP NOT NULL,
                         first_name VARCHAR(255) NOT NULL,
                         last_name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL,
                         phone VARCHAR(255) NOT NULL,
                         app_user_id BIGINT NOT NULL,
                         hotel_room_id BIGINT NOT NULL,
                         guest_country_id BIGINT NOT NULL,
                         status_id BIGINT NOT NULL,
                         FOREIGN KEY (status_id) REFERENCES booking_status(id),
                         FOREIGN KEY (guest_country_id) REFERENCES country(id),
                         FOREIGN KEY (app_user_id) REFERENCES app_user(id),
                         FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id)
);

CREATE TABLE review (
                        id BIGSERIAL PRIMARY KEY,
                        rate NUMERIC(3, 1) NOT NULL,
                        content TEXT NOT NULL,
                        pros VARCHAR(255),
                        cons VARCHAR(255),
                        app_user_id BIGINT NOT NULL,
                        hotel_id BIGINT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (app_user_id) REFERENCES app_user(id),
                        FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE recently_viewed_hotel (
                                       id BIGSERIAL PRIMARY KEY,
                                       app_user_id BIGINT NOT NULL,
                                       hotel_id BIGINT NOT NULL,
                                       viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (app_user_id) REFERENCES app_user(id),
                                       FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE favorites (
                           id BIGSERIAL PRIMARY KEY,
                           app_user_id BIGINT NOT NULL,
                           hotel_id BIGINT NOT NULL,
                           FOREIGN KEY (app_user_id) REFERENCES app_user(id),
                           FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE email_verification (
                                    id BIGSERIAL PRIMARY KEY,
                                    token VARCHAR(255),
                                    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                    app_user_id BIGINT NOT NULL,
                                    expires_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP + INTERVAL '1 day',
                                    FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);

-- Payment-related tables added below
CREATE TABLE payment_status (
                                id BIGSERIAL PRIMARY KEY,
                                status VARCHAR(50) NOT NULL UNIQUE
);

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

CREATE TABLE bank_card (
                           id BIGSERIAL PRIMARY KEY,
                           card_number VARCHAR(16) NOT NULL,
                           card_holder_name VARCHAR(255) NOT NULL,
                           expiration_date DATE NOT NULL,
                           cvv VARCHAR(3) NOT NULL,
                           app_user_id BIGINT NOT NULL,
                           FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);

CREATE TABLE booking_payment_option (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                        option VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE hotel_booking_payment_option_link (
                                                   id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                                   hotel_id BIGINT NOT NULL,
                                                   booking_payment_option_id BIGINT NOT NULL,
                                                   FOREIGN KEY (hotel_id) REFERENCES hotel(id),
                                                   FOREIGN KEY (booking_payment_option_id) REFERENCES booking_payment_option(id)
);