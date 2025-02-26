CREATE TABLE country (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL
);

CREATE TABLE place_of_birth (
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
    citizenship_id BIGINT,
    residency_id BIGINT,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (citizenship_id) REFERENCES country(id),
    FOREIGN KEY (residency_id) REFERENCES address(id)
);

CREATE TABLE passport (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    passport_number VARCHAR(255) NOT NULL,
    expiration_date DATE NOT NULL,
    country_of_issuance_id BIGINT NOT NULL,
    app_user_id BIGINT,
    FOREIGN KEY (country_of_issuance_id) REFERENCES country(id),
    FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);

CREATE TABLE hotel (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_id BIGINT NOT NULL,
    short_desc TEXT NOT NULL,
    full_desc TEXT NOT NULL,
    stars INT,
    main_image_url VARCHAR(255),
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
    metering NUMERIC(10, 2) NOT NULL,
    is_children_friendly BOOLEAN NOT NULL DEFAULT FALSE,
    check_in_time TIME NOT NULL,
    check_out_time TIME NOT NULL,
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

CREATE TABLE room_facility (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE room_bed (
      id BIGSERIAL PRIMARY KEY,
      bed_type VARCHAR(255) NOT NULL
);

CREATE TABLE hotel_room_room_facility (
    id BIGSERIAL PRIMARY KEY,
    hotel_room_id BIGINT NOT NULL,
    room_facility_id BIGINT NOT NULL,
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
    FOREIGN KEY (room_facility_id) REFERENCES room_facility(id)
);

CREATE TABLE hotel_room_room_bed (
    id BIGSERIAL PRIMARY KEY,
    hotel_room_id INT NOT NULL,
    room_bed_id INT NOT NULL,
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
    FOREIGN KEY (room_bed_id) REFERENCES room_bed(id)
);

CREATE TABLE room_highlight (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE hotel_room_room_highlight (
    id BIGSERIAL PRIMARY KEY,
    hotel_room_id BIGINT NOT NULL,
    room_highlight_id BIGINT NOT NULL,
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
    FOREIGN KEY (room_highlight_id) REFERENCES room_highlight(id)
);

CREATE TABLE hotel_highlight (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE,
     image_url VARCHAR(255) NOT NULL
);

CREATE TABLE hotel_hotel_highlight (
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
    phone_number VARCHAR(255) NOT NULL,
    app_user_id BIGINT NOT NULL,
    hotel_room_id BIGINT NOT NULL,
    country_id BIGINT NOT NULL,
    booking_status_id BIGINT NOT NULL,
    FOREIGN KEY (booking_status_id) REFERENCES booking_status(id),
    FOREIGN KEY (country_id) REFERENCES country(id),
    FOREIGN KEY (app_user_id) REFERENCES app_user(id),
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id)
);

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    rate NUMERIC(3, 1) NOT NULL,
    content TEXT NOT NULL,
    good_sides VARCHAR(255),
    bad_sides VARCHAR(255),
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
    app_user_id BIGSERIAL NOT NULL,
    expires_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP + INTERVAL '1 day',
    FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);