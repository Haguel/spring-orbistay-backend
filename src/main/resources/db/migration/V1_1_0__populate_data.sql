-- Insert countries
INSERT INTO country (name, code) VALUES
                                     ('United States', 'US'),
                                     ('Switzerland', 'CH');

-- Insert addresses for United States
INSERT INTO address (city, street, country_id) VALUES
                                                   ('New York', '123 Main St', 1),
                                                   ('New York', '456 Broadway', 1),
                                                   ('New York', '789 Wall St', 1),
                                                   ('Zurich', '117 Bahnhofstrasse', 2),
                                                   ('Zurich', '118 Bahnhofstrasse', 2),
                                                   ('Zurich', '119 Bahnhofstrasse', 2);

-- Insert hotels for each address with updated descriptions
INSERT INTO hotel (name, address_id, short_desc, full_desc, stars, main_image_url) VALUES
                                                                                       ('Hotel New York 1', 1, 'Cozy hotel in New York', 'A cozy hotel in New York with modern amenities, a beautiful city view, and excellent service.', 4, 'hotel_ny1.jpg'),
                                                                                       ('Hotel New York 2', 2, 'Luxurious hotel in New York', 'A luxurious hotel in New York offering world-class service, elegant rooms, and top-notch facilities.', 5, 'hotel_ny2.jpg'),
                                                                                       ('Hotel New York 3', 3, 'Budget-friendly hotel in New York', 'A budget-friendly hotel in New York with comfortable rooms, great service, and convenient location.', 3, 'hotel_ny3.jpg'),
                                                                                       ('Hotel Zurich 1', 4, 'Charming hotel in Zurich', 'A charming hotel in Zurich blending modern and traditional Swiss hospitality, with beautiful surroundings.', 4, 'hotel_zurich1.jpg'),
                                                                                       ('Hotel Zurich 2', 5, 'Premium hotel in Zurich', 'A premium hotel in Zurich offering luxurious rooms, top-notch amenities, and exceptional service.', 5, 'hotel_zurich2.jpg'),
                                                                                       ('Hotel Zurich 3', 6, 'Affordable hotel in Zurich', 'An affordable hotel in Zurich with clean rooms, friendly staff, and a convenient location.', 3, 'hotel_zurich3.jpg');

-- Insert hotel rooms
-- Insert hotel rooms for each hotel with cheaper prices
INSERT INTO hotel_room (name, description, cost_per_day, is_children_friendly, capacity, images_url, hotel_id) VALUES
                                                                                                                   -- Hotel New York 1
                                                                                                                   ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, ARRAY['deluxe_room_ny1.jpg'], 1),
                                                                                                                   ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, ARRAY['standard_room_ny1.jpg'], 1),
                                                                                                                   ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, ARRAY['suite_ny1.jpg'], 1),
                                                                                                                   ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, ARRAY['family_room_ny1.jpg'], 1),

                                                                                                                   -- Hotel New York 2
                                                                                                                   ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, ARRAY['deluxe_room_ny2.jpg'], 2),
                                                                                                                   ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, ARRAY['standard_room_ny2.jpg'], 2),
                                                                                                                   ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, ARRAY['suite_ny2.jpg'], 2),
                                                                                                                   ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, ARRAY['family_room_ny2.jpg'], 2),

                                                                                                                   -- Hotel New York 3
                                                                                                                   ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, ARRAY['deluxe_room_ny3.jpg'], 3),
                                                                                                                   ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, ARRAY['standard_room_ny3.jpg'], 3),
                                                                                                                   ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, ARRAY['suite_ny3.jpg'], 3),
                                                                                                                   ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, ARRAY['family_room_ny3.jpg'], 3),

                                                                                                                   -- Hotel Zurich 1
                                                                                                                   ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, ARRAY['deluxe_room_zurich1.jpg'], 4),
                                                                                                                   ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, ARRAY['standard_room_zurich1.jpg'], 4),
                                                                                                                   ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, ARRAY['suite_zurich1.jpg'], 4),
                                                                                                                   ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, ARRAY['family_room_zurich1.jpg'], 4),

                                                                                                                   -- Hotel Zurich 2
                                                                                                                   ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, ARRAY['deluxe_room_zurich2.jpg'], 5),
                                                                                                                   ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, ARRAY['standard_room_zurich2.jpg'], 5),
                                                                                                                   ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, ARRAY['suite_zurich2.jpg'], 5),
                                                                                                                   ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, ARRAY['family_room_zurich2.jpg'], 5),

                                                                                                                   -- Hotel Zurich 3
                                                                                                                   ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, ARRAY['deluxe_room_zurich3.jpg'], 6),
                                                                                                                   ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, ARRAY['standard_room_zurich3.jpg'], 6),
                                                                                                                   ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, ARRAY['suite_zurich3.jpg'], 6),
                                                                                                                   ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, ARRAY['family_room_zurich3.jpg'], 6);

-- Insert room facilities
INSERT INTO room_facility (name) VALUES
                                     ('Free WiFi'),
                                     ('Air Conditioning'),
                                     ('Flat-Screen TV'),
                                     ('Mini Bar'),
                                     ('Room Service'),
                                     ('Coffee Maker'),
                                     ('Safe'),
                                     ('Hair Dryer'),
                                     ('Iron and Ironing Board'),
                                     ('Bathrobe and Slippers'),
                                     ('Complimentary Toiletries'),
                                     ('Desk'),
                                     ('Seating Area'),
                                     ('Soundproofing'),
                                     ('Balcony'),
                                     ('City View'),
                                     ('Garden View'),
                                     ('Pool View'),
                                     ('Mountain View'),
                                     ('Ocean View'),
                                     ('Heating'),
                                     ('Fan'),
                                     ('Private Bathroom'),
                                     ('Shared Bathroom'),
                                     ('Shower'),
                                     ('Bathtub'),
                                     ('Spa Bath'),
                                     ('Hot Tub'),
                                     ('Sauna'),
                                     ('Kitchenette'),
                                     ('Full Kitchen'),
                                     ('Microwave'),
                                     ('Refrigerator'),
                                     ('Dishwasher'),
                                     ('Washing Machine'),
                                     ('Dryer'),
                                     ('Fireplace'),
                                     ('Sofa'),
                                     ('Dining Area'),
                                     ('Outdoor Furniture'),
                                     ('Terrace'),
                                     ('Patio'),
                                     ('BBQ Facilities'),
                                     ('Private Entrance'),
                                     ('Wardrobe or Closet'),
                                     ('Alarm Clock'),
                                     ('Electric Kettle'),
                                     ('Toaster'),
                                     ('Stovetop'),
                                     ('Oven');

INSERT INTO hotel_room_room_facility (hotel_room_id, room_facility_id)
SELECT
    hr.id,
    rf.id
FROM
    hotel_room hr
        JOIN room_facility rf ON 1=1
WHERE
    (hr.name LIKE '%Deluxe%' OR hr.name LIKE '%Suite%')
  AND rf.name IN ('Free WiFi', 'Shower', 'Air Conditioning', 'Flat-Screen TV', 'Mini Bar', 'Room Service', 'Coffee Maker', 'Safe', 'Hair Dryer', 'Iron and Ironing Board', 'Bathrobe and Slippers', 'Complimentary Toiletries', 'Desk', 'Seating Area', 'Soundproofing', 'Balcony', 'City View', 'Spa Bath', 'Hot Tub')
UNION ALL
SELECT
    hr.id,
    rf.id
FROM
    hotel_room hr
        JOIN room_facility rf ON 1=1
WHERE
    hr.name LIKE '%Standard%'
  AND rf.name IN ('Free WiFi', 'Shower', 'Air Conditioning', 'Flat-Screen TV', 'Coffee Maker', 'Safe', 'Hair Dryer', 'Desk', 'Seating Area', 'Soundproofing', 'Sauna')
UNION ALL
SELECT
    hr.id,
    rf.id
FROM
    hotel_room hr
        JOIN room_facility rf ON 1=1
WHERE
    hr.name LIKE '%Family%'
  AND rf.name IN ('Free WiFi', 'Shower', 'Air Conditioning', 'Flat-Screen TV', 'Coffee Maker', 'Safe', 'Hair Dryer', 'Desk', 'Seating Area', 'Soundproofing', 'Kitchenette');


INSERT INTO room_highlight (name, image_url) VALUES
                                                 ('King-sized Bed', 'king_bed.jpg'),
                                                 ('Queen-sized Bed', 'queen_bed.jpg'),
                                                 ('Work Desk', 'work_desk.jpg'),
                                                 ('Free Wi-Fi', 'free_wifi.jpg'),
                                                 ('Beautiful View', 'beautiful_view.jpg'),
                                                 ('Antique Furniture', 'antique_furniture.jpg'),
                                                 ('Modern Amenities', 'modern_amenities.jpg'),
                                                 ('Private Balcony', 'private_balcony.jpg'),
                                                 ('Spacious Living Area', 'spacious_living_area.jpg'),
                                                 ('Luxurious Bathroom', 'luxurious_bathroom.jpg'),
                                                 ('Mini-bar', 'mini_bar.jpg'),
                                                 ('Stunning City View', 'stunning_city_view.jpg'),
                                                 ('Comfortable Bed', 'comfortable_bed.jpg'),
                                                 ('Ergonomic Chair', 'ergonomic_chair.jpg'),
                                                 ('Complimentary Breakfast', 'complimentary_breakfast.jpg'),
                                                 ('Kitchenette', 'kitchenette.jpg'),
                                                 ('Two Bedrooms', 'two_bedrooms.jpg'),
                                                 ('Living Area', 'living_area.jpg'),
                                                 ('Free Parking', 'free_parking.jpg'),
                                                 ('Swimming Pool Access', 'swimming_pool_access.jpg'),
                                                 ('Gym Access', 'gym_access.jpg'),
                                                 ('Room Service', 'room_service.jpg'),
                                                 ('Air Conditioning', 'air_conditioning.jpg'),
                                                 ('Heating', 'heating.jpg'),
                                                 ('Flat-screen TV', 'flat_screen_tv.jpg'),
                                                 ('Coffee Maker', 'coffee_maker.jpg'),
                                                 ('Safe', 'safe.jpg'),
                                                 ('Hair Dryer', 'hair_dryer.jpg'),
                                                 ('Ironing Facilities', 'ironing_facilities.jpg'),
                                                 ('Bathrobe', 'bathrobe.jpg'),
                                                 ('Slippers', 'slippers.jpg'),
                                                 ('Soundproof Rooms', 'soundproof_rooms.jpg'),
                                                 ('Hypoallergenic Bedding', 'hypoallergenic_bedding.jpg'),
                                                 ('Blackout Curtains', 'blackout_curtains.jpg'),
                                                 ('Desk Lamp', 'desk_lamp.jpg'),
                                                 ('Telephone', 'telephone.jpg'),
                                                 ('Wake-up Service', 'wake_up_service.jpg'),
                                                 ('Daily Housekeeping', 'daily_housekeeping.jpg'),
                                                 ('Laundry Service', 'laundry_service.jpg'),
                                                 ('Dry Cleaning', 'dry_cleaning.jpg'),
                                                 ('Iron', 'iron.jpg'),
                                                 ('Microwave', 'microwave.jpg'),
                                                 ('Refrigerator', 'refrigerator.jpg'),
                                                 ('Toaster', 'toaster.jpg'),
                                                 ('Dishwasher', 'dishwasher.jpg'),
                                                 ('Oven', 'oven.jpg'),
                                                 ('Stovetop', 'stovetop.jpg'),
                                                 ('Dining Table', 'dining_table.jpg'),
                                                 ('Sofa', 'sofa.jpg');

INSERT INTO hotel_room_room_highlight (hotel_room_id, room_highlight_id)
SELECT
    hr.id,
    rh.id
FROM
    hotel_room hr
        JOIN room_highlight rh ON 1=1
WHERE
    (hr.name LIKE '%Deluxe%' OR hr.name LIKE '%Suite%')
  AND rh.name IN ('King-sized Bed', 'Beautiful View', 'Antique Furniture', 'Modern Amenities', 'Private Balcony', 'Spacious Living Area', 'Luxurious Bathroom', 'Stunning City View', 'Complimentary Breakfast')
UNION ALL
SELECT
    hr.id,
    rh.id
FROM
    hotel_room hr
        JOIN room_highlight rh ON 1=1
WHERE
    hr.name LIKE '%Standard%'
  AND rh.name IN ('Queen-sized Bed', 'Comfortable Bed', 'Work Desk', 'Free Wi-Fi', 'Ergonomic Chair')
UNION ALL
SELECT
    hr.id,
    rh.id
FROM
    hotel_room hr
        JOIN room_highlight rh ON 1=1
WHERE
    hr.name LIKE '%Family%'
  AND rh.name IN ('Two Bedrooms', 'Living Area', 'Kitchenette', 'Free Parking', 'Swimming Pool Access', 'Gym Access');


-- Insert hotel highlights
INSERT INTO hotel_highlight (name, image_url) VALUES
                                                  ('Free Breakfast', 'free_breakfast.jpg'),
                                                  ('Swimming Pool', 'swimming_pool.jpg'),
                                                  ('Spa Services', 'spa_services.jpg'),
                                                  ('Fitness Center', 'fitness_center.jpg'),
                                                  ('Pet Friendly', 'pet_friendly.jpg'),
                                                  ('Free Parking', 'free_parking.jpg'),
                                                  ('Airport Shuttle', 'airport_shuttle.jpg'),
                                                  ('24/7 Room Service', 'room_service.jpg'),
                                                  ('Business Center', 'business_center.jpg'),
                                                  ('Concierge Service', 'concierge_service.jpg'),
                                                  ('Rooftop Bar', 'rooftop_bar.jpg'),
                                                  ('Kids Club', 'kids_club.jpg'),
                                                  ('Live Entertainment', 'live_entertainment.jpg'),
                                                  ('Eco-Friendly', 'eco_friendly.jpg'),
                                                  ('Private Beach', 'private_beach.jpg');

INSERT INTO hotel_hotel_highlight (hotel_id, hotel_highlight_id)
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.stars = 5
  AND hh.name IN ('Free Breakfast', 'Swimming Pool', 'Spa Services', 'Fitness Center', 'Concierge Service', 'Rooftop Bar', 'Live Entertainment')
UNION ALL
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.stars = 4
  AND hh.name IN ('Free Breakfast', 'Swimming Pool', 'Fitness Center', 'Concierge Service', 'Rooftop Bar')
UNION ALL
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.stars = 3
  AND hh.name IN ('Free Breakfast', 'Fitness Center')
UNION ALL
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.short_desc LIKE '%pet friendly%'
  AND hh.name = 'Pet Friendly'
UNION ALL
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.full_desc LIKE '%airport shuttle%'
  AND hh.name = 'Airport Shuttle'
UNION ALL
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.full_desc LIKE '%business center%'
  AND hh.name = 'Business Center'
UNION ALL
SELECT
    h.id,
    hh.id
FROM
    hotel h
        JOIN hotel_highlight hh ON 1=1
WHERE
    h.full_desc LIKE '%eco-friendly%'
  AND hh.name = 'Eco-Friendly';
