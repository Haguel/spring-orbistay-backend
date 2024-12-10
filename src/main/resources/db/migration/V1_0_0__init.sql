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
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255) NOT NULL UNIQUE,
    avatar_url VARCHAR(255),
    citizenship_id BIGINT,
    residency_id BIGINT,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (citizenship_id) REFERENCES country(id),
    FOREIGN KEY (residency_id) REFERENCES address(id)
);

CREATE TABLE passport (
    id BIGSERIAL PRIMARY KEY,
    country_of_issuance_id BIGINT NOT NULL,
    date_of_issue DATE NOT NULL,
    expiration_date DATE NOT NULL,
    holder_full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    nationality VARCHAR(255) NOT NULL,
    place_of_birth_id BIGINT NOT NULL,
    gender VARCHAR(50) NOT NULL,
    app_user_id BIGINT,
    FOREIGN KEY (country_of_issuance_id) REFERENCES country(id),
    FOREIGN KEY (place_of_birth_id) REFERENCES place_of_birth(id),
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

CREATE TABLE hotel_room (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    cost_per_day NUMERIC(10, 2) NOT NULL,
    is_children_friendly BOOLEAN NOT NULL DEFAULT FALSE,
    capacity INT NOT NULL,
    images_url TEXT[]
);

CREATE TABLE room_facility (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE hotel_room_room_facility (
    id BIGSERIAL PRIMARY KEY,
    hotel_room_id BIGINT NOT NULL,
    room_facility_id BIGINT NOT NULL,
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
    FOREIGN KEY (room_facility_id) REFERENCES room_facility(id)
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

CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY,
    check_in TIMESTAMP NOT NULL,
    check_out TIMESTAMP NOT NULL,
    app_user_id BIGINT NOT NULL,
    hotel_room_id BIGINT NOT NULL,
    FOREIGN KEY (app_user_id) REFERENCES app_user(id),
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id)
);

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    rate NUMERIC(2, 1) NOT NULL,
    content TEXT,
    app_user_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    FOREIGN KEY (app_user_id) REFERENCES app_user(id),
    FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);