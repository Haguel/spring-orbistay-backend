-- V1_1_1__add_paris_hotels.sql

-- Insert new addresses for Paris, France (country_id = 62)
INSERT INTO address (city, street, country_id) VALUES
                                                   ('Paris', '12 Rue de Rivoli', 62),
                                                   ('Paris', '45 Avenue des Champs-Élysées', 62),
                                                   ('Paris', '78 Boulevard Saint-Germain', 62);

-- Insert three new hotels in Paris
INSERT INTO hotel (name, address_id, short_desc, full_desc, stars, main_image_url, check_in_time, check_out_time) VALUES
                                                                  ('Hotel Paris Lumière', (SELECT id FROM address WHERE street = '12 Rue de Rivoli'),
                                                                   'Elegant hotel in Paris',
                                                                   'An elegant hotel in the heart of Paris with stylish rooms, modern amenities, and a breathtaking view of the city.',
                                                                   4, 'https://orbistayblob.blob.core.windows.net/hotels/paris_1_1.jpg', '14:00:00', '12:00:00'),
                                                                  ('Hotel Paris Étoile', (SELECT id FROM address WHERE street = '45 Avenue des Champs-Élysées'),
                                                                   'Luxury hotel in Paris',
                                                                   'A luxurious hotel on the iconic Champs-Élysées, offering premium services, exquisite dining, and unparalleled comfort.',
                                                                   5, 'https://orbistayblob.blob.core.windows.net/hotels/paris_2_1.jpg', '15:00:00', '11:00:00'),
                                                                  ('Hotel Paris Cozy', (SELECT id FROM address WHERE street = '78 Boulevard Saint-Germain'),
                                                                   'Affordable hotel in Paris',
                                                                   'An affordable yet charming hotel in Paris with cozy rooms, friendly staff, and a perfect location for exploring the city.',
                                                                   3, 'https://orbistayblob.blob.core.windows.net/hotels/paris_3_1.jpg', '16:00:00', '10:00:00');

INSERT INTO hotel_image (hotel_id, image_url) VALUES
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière') , 'https://orbistayblob.blob.core.windows.net/hotels/paris_1_1.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_1_2.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_1_3.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_1_4.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_1_5.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_2_1.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_2_2.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_2_3.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_2_4.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_2_5.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_3_1.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_3_2.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_3_3.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_3_4.jpg'),
                                                  ((SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), 'https://orbistayblob.blob.core.windows.net/hotels/paris_3_5.jpg');


-- Insert rooms for the new hotels (same structure as V1_1_0)
INSERT INTO hotel_room (name, description, cost_per_night, is_children_friendly, capacity, hotel_id, metering) VALUES
                               -- Hotel Paris Lumière
                               ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), (20 + random() * 50)::numeric),
                               ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), (20 + random() * 50)::numeric),
                               ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), (20 + random() * 50)::numeric),
                               ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière'), (20 + random() * 50)::numeric),

                               -- Hotel Paris Étoile
                               ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), (20 + random() * 50)::numeric),
                               ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), (20 + random() * 50)::numeric),
                               ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), (20 + random() * 50)::numeric),
                               ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile'), (20 + random() * 50)::numeric),

                               -- Hotel Paris Cozy
                               ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), (20 + random() * 50)::numeric),
                               ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, false, 2, (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), (20 + random() * 50)::numeric),
                               ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), (20 + random() * 50)::numeric),
                               ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy'), (20 + random() * 50)::numeric);

-- Insert hotel room images
INSERT INTO hotel_room_image (hotel_room_id, image_url) VALUES
                                                            -- Hotel Paris Lumière
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_7_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_7_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_7_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_7_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_7_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_7_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_7_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_7_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_7_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_7_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_7_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_7_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_7_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_7_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_7_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_7_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_7_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_7_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_7_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_7_5.jpg'),

                                                            -- Hotel Paris Étoile
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_8_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_8_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_8_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_8_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_8_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_8_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_8_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_8_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_8_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_8_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_8_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_8_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_8_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_8_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_8_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_8_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_8_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_8_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_8_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_8_5.jpg'),

                                                            -- Hotel Paris Cozy
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_9_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_9_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_9_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_9_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/deluxe_room_9_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_9_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_9_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_9_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/budget_room_9_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_9_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_9_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_9_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_9_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/suite_room_9_5.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_9_1.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_9_2.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_9_3.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_9_4.jpg'),
                                                            ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 'https://orbistayblob.blob.core.windows.net/rooms/family_room_9_5.jpg');

INSERT INTO hotel_room_room_bed (hotel_room_id, room_bed_id) VALUES
                                                                 -- Hotel Paris Lumière
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Lumière')), 2), -- Double

                                                                 -- Hotel Paris Étoile
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Étoile')), 2), -- Double

                                                                 -- Hotel Paris Cozy
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Deluxe Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Standard Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Suite' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 2), -- Double
                                                                 ((SELECT id FROM hotel_room WHERE name = 'Family Room' AND hotel_id = (SELECT id FROM hotel WHERE name = 'Hotel Paris Cozy')), 2); -- Double


UPDATE hotel SET name = 'The Manhattan Haven' WHERE id = 1;
UPDATE hotel SET name = 'Empire Luxe Hotel' WHERE id = 2;
UPDATE hotel SET name = 'Big Apple Retreat' WHERE id = 3;
UPDATE hotel SET name = 'Alpine Glow Hotel' WHERE id = 4;
UPDATE hotel SET name = 'Zurich Grand Palace' WHERE id = 5;
UPDATE hotel SET name = 'Swiss Comfort Inn' WHERE id = 6;