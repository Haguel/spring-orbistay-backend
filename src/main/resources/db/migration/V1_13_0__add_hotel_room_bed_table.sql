CREATE TABLE room_bed (
    id BIGSERIAL PRIMARY KEY,
    bed_type VARCHAR(255) NOT NULL
);

INSERT INTO room_bed (bed_type)
    VALUES ('SINGLE'),
           ('DOUBLE');

CREATE TABLE hotel_room_room_bed (
    id BIGSERIAL PRIMARY KEY,
    hotel_room_id INT NOT NULL,
    room_bed_id INT NOT NULL,
    FOREIGN KEY (hotel_room_id) REFERENCES hotel_room(id),
    FOREIGN KEY (room_bed_id) REFERENCES room_bed(id)
);


INSERT INTO hotel_room_room_bed (hotel_room_id, room_bed_id)
VALUES
    -- Hotel New York 1
    (1, 2),
    (2, 2),
    (3, 1),
    (4, 2),
    (4, 2),

    -- Hotel New York 2
    (5, 2),
    (6, 2),
    (7, 1),
    (8, 2),
    (8, 2),

    -- Hotel New York 3
    (9, 2),
    (10, 2),
    (11, 1),
    (12, 2),
    (12, 2),

    -- Hotel Zurich 1
    (13, 2),
    (14, 2),
    (15, 1),
    (16, 2),
    (16, 2),

    -- Hotel Zurich 2
    (17, 2),
    (18, 2),
    (19, 1),
    (20, 2),
    (20, 2),

    -- Hotel Zurich 3
    (21, 2),
    (22, 2),
    (23, 1),
    (24, 2),
    (24, 2)
