-- Data migration script for the Booking clone database with updated names
-- Date: March 01, 2025
-- Notes: Adjusted to fit the revised initial migration with new table and column names.

-- Insert booking statuses
INSERT INTO booking_status (status) VALUES
                                        ('ACTIVE'),
                                        ('CHECKED-IN'),
                                        ('CHECKED-OUT'),
                                        ('CANCELED'),
                                        ('PENDING');

-- Insert countries
INSERT INTO country (name, code) VALUES
                                     ('United States', 'US'),
                                     ('Switzerland', 'CH'),
                                     ('Afghanistan', 'AF'),
                                     ('Albania', 'AL'),
                                     ('Algeria', 'DZ'),
                                     ('Andorra', 'AD'),
                                     ('Angola', 'AO'),
                                     ('Antigua and Barbuda', 'AG'),
                                     ('Argentina', 'AR'),
                                     ('Armenia', 'AM'),
                                     ('Australia', 'AU'),
                                     ('Austria', 'AT'),
                                     ('Azerbaijan', 'AZ'),
                                     ('Bahamas', 'BS'),
                                     ('Bahrain', 'BH'),
                                     ('Bangladesh', 'BD'),
                                     ('Barbados', 'BB'),
                                     ('Belarus', 'BY'),
                                     ('Belgium', 'BE'),
                                     ('Belize', 'BZ'),
                                     ('Benin', 'BJ'),
                                     ('Bhutan', 'BT'),
                                     ('Bolivia', 'BO'),
                                     ('Bosnia and Herzegovina', 'BA'),
                                     ('Botswana', 'BW'),
                                     ('Brazil', 'BR'),
                                     ('Brunei', 'BN'),
                                     ('Bulgaria', 'BG'),
                                     ('Burkina Faso', 'BF'),
                                     ('Burundi', 'BI'),
                                     ('Cabo Verde', 'CV'),
                                     ('Cambodia', 'KH'),
                                     ('Cameroon', 'CM'),
                                     ('Canada', 'CA'),
                                     ('Central African Republic', 'CF'),
                                     ('Chad', 'TD'),
                                     ('Chile', 'CL'),
                                     ('China', 'CN'),
                                     ('Colombia', 'CO'),
                                     ('Comoros', 'KM'),
                                     ('Congo, Democratic Republic of the', 'CD'),
                                     ('Congo, Republic of the', 'CG'),
                                     ('Costa Rica', 'CR'),
                                     ('Croatia', 'HR'),
                                     ('Cuba', 'CU'),
                                     ('Cyprus', 'CY'),
                                     ('Czech Republic', 'CZ'),
                                     ('Denmark', 'DK'),
                                     ('Djibouti', 'DJ'),
                                     ('Dominica', 'DM'),
                                     ('Dominican Republic', 'DO'),
                                     ('Ecuador', 'EC'),
                                     ('Egypt', 'EG'),
                                     ('El Salvador', 'SV'),
                                     ('Equatorial Guinea', 'GQ'),
                                     ('Eritrea', 'ER'),
                                     ('Estonia', 'EE'),
                                     ('Eswatini', 'SZ'),
                                     ('Ethiopia', 'ET'),
                                     ('Fiji', 'FJ'),
                                     ('Finland', 'FI'),
                                     ('France', 'FR'),
                                     ('Gabon', 'GA'),
                                     ('Gambia', 'GM'),
                                     ('Georgia', 'GE'),
                                     ('Germany', 'DE'),
                                     ('Ghana', 'GH'),
                                     ('Greece', 'GR'),
                                     ('Grenada', 'GD'),
                                     ('Guatemala', 'GT'),
                                     ('Guinea', 'GN'),
                                     ('Guinea-Bissau', 'GW'),
                                     ('Guyana', 'GY'),
                                     ('Haiti', 'HT'),
                                     ('Honduras', 'HN'),
                                     ('Hungary', 'HU'),
                                     ('Iceland', 'IS'),
                                     ('India', 'IN'),
                                     ('Indonesia', 'ID'),
                                     ('Iran', 'IR'),
                                     ('Iraq', 'IQ'),
                                     ('Ireland', 'IE'),
                                     ('Israel', 'IL'),
                                     ('Italy', 'IT'),
                                     ('Jamaica', 'JM'),
                                     ('Japan', 'JP'),
                                     ('Jordan', 'JO'),
                                     ('Kazakhstan', 'KZ'),
                                     ('Kenya', 'KE'),
                                     ('Kiribati', 'KI'),
                                     ('Korea, North', 'KP'),
                                     ('Korea, South', 'KR'),
                                     ('Kosovo', 'XK'),
                                     ('Kuwait', 'KW'),
                                     ('Kyrgyzstan', 'KG'),
                                     ('Laos', 'LA'),
                                     ('Latvia', 'LV'),
                                     ('Lebanon', 'LB'),
                                     ('Lesotho', 'LS'),
                                     ('Liberia', 'LR'),
                                     ('Libya', 'LY'),
                                     ('Liechtenstein', 'LI'),
                                     ('Lithuania', 'LT'),
                                     ('Luxembourg', 'LU'),
                                     ('Madagascar', 'MG'),
                                     ('Malawi', 'MW'),
                                     ('Malaysia', 'MY'),
                                     ('Maldives', 'MV'),
                                     ('Mali', 'ML'),
                                     ('Malta', 'MT'),
                                     ('Marshall Islands', 'MH'),
                                     ('Mauritania', 'MR'),
                                     ('Mauritius', 'MU'),
                                     ('Mexico', 'MX'),
                                     ('Micronesia', 'FM'),
                                     ('Moldova', 'MD'),
                                     ('Monaco', 'MC'),
                                     ('Mongolia', 'MN'),
                                     ('Montenegro', 'ME'),
                                     ('Morocco', 'MA'),
                                     ('Mozambique', 'MZ'),
                                     ('Myanmar', 'MM'),
                                     ('Namibia', 'NA'),
                                     ('Nauru', 'NR'),
                                     ('Nepal', 'NP'),
                                     ('Netherlands', 'NL'),
                                     ('New Zealand', 'NZ'),
                                     ('Nicaragua', 'NI'),
                                     ('Niger', 'NE'),
                                     ('Nigeria', 'NG'),
                                     ('North Macedonia', 'MK'),
                                     ('Norway', 'NO'),
                                     ('Oman', 'OM'),
                                     ('Pakistan', 'PK'),
                                     ('Palau', 'PW'),
                                     ('Panama', 'PA'),
                                     ('Papua New Guinea', 'PG'),
                                     ('Paraguay', 'PY'),
                                     ('Peru', 'PE'),
                                     ('Philippines', 'PH'),
                                     ('Poland', 'PL'),
                                     ('Portugal', 'PT'),
                                     ('Qatar', 'QA'),
                                     ('Romania', 'RO'),
                                     ('Russia', 'RU'),
                                     ('Rwanda', 'RW'),
                                     ('Saint Kitts and Nevis', 'KN'),
                                     ('Saint Lucia', 'LC'),
                                     ('Saint Vincent and the Grenadines', 'VC'),
                                     ('Samoa', 'WS'),
                                     ('San Marino', 'SM'),
                                     ('Sao Tome and Principe', 'ST'),
                                     ('Saudi Arabia', 'SA'),
                                     ('Senegal', 'SN'),
                                     ('Serbia', 'RS'),
                                     ('Seychelles', 'SC'),
                                     ('Sierra Leone', 'SL'),
                                     ('Singapore', 'SG'),
                                     ('Slovakia', 'SK'),
                                     ('Slovenia', 'SI'),
                                     ('Solomon Islands', 'SB'),
                                     ('Somalia', 'SO'),
                                     ('South Africa', 'ZA'),
                                     ('South Sudan', 'SS'),
                                     ('Spain', 'ES'),
                                     ('Sri Lanka', 'LK'),
                                     ('Sudan', 'SD'),
                                     ('Suriname', 'SR'),
                                     ('Sweden', 'SE'),
                                     ('Syria', 'SY'),
                                     ('Taiwan', 'TW'),
                                     ('Tajikistan', 'TJ'),
                                     ('Tanzania', 'TZ'),
                                     ('Thailand', 'TH'),
                                     ('Timor-Leste', 'TL'),
                                     ('Togo', 'TG'),
                                     ('Tonga', 'TO'),
                                     ('Trinidad and Tobago', 'TT'),
                                     ('Tunisia', 'TN'),
                                     ('Turkey', 'TR'),
                                     ('Turkmenistan', 'TM'),
                                     ('Tuvalu', 'TV'),
                                     ('Uganda', 'UG'),
                                     ('Ukraine', 'UA'),
                                     ('United Arab Emirates', 'AE'),
                                     ('United Kingdom', 'GB'),
                                     ('Uruguay', 'UY'),
                                     ('Uzbekistan', 'UZ'),
                                     ('Vanuatu', 'VU'),
                                     ('Vatican City', 'VA'),
                                     ('Venezuela', 'VE'),
                                     ('Vietnam', 'VN'),
                                     ('Yemen', 'YE'),
                                     ('Zambia', 'ZM'),
                                     ('Zimbabwe', 'ZW');

-- Insert addresses for United States and Switzerland
INSERT INTO address (city, street, country_id) VALUES
                                                   ('New York', '123 Main St', 1),
                                                   ('New York', '456 Broadway', 1),
                                                   ('New York', '789 Wall St', 1),
                                                   ('Zurich', '117 Bahnhofstrasse', 2),
                                                   ('Zurich', '118 Bahnhofstrasse', 2),
                                                   ('Zurich', '119 Bahnhofstrasse', 2),
                                                   ('Los Angeles', '101 Hollywood Blvd', 1),
                                                   ('Chicago', '202 Michigan Ave', 1),
                                                   ('San Francisco', '303 Market St', 1);

-- Insert hotels for each address with updated descriptions
INSERT INTO hotel (name, address_id, short_desc, full_desc, stars, main_image_url, check_in_time, check_out_time) VALUES
                                                                                                                      ('Hotel New York 1', 1, 'Cozy hotel in New York', 'A cozy hotel in New York with modern amenities, a beautiful city view, and excellent service.', 4, 'hotel_ny1.jpg', '14:00:00', '12:00:00'),
                                                                                                                      ('Hotel New York 2', 2, 'Luxurious hotel in New York', 'A luxurious hotel in New York offering world-class service, elegant rooms, and top-notch facilities.', 5, 'hotel_ny2.jpg', '15:00:00', '11:00:00'),
                                                                                                                      ('Hotel New York 3', 3, 'Budget-friendly hotel in New York', 'A budget-friendly hotel in New York with comfortable rooms, great service, and convenient location.', 3, 'hotel_ny3.jpg', '16:00:00', '10:00:00'),
                                                                                                                      ('Hotel Zurich 1', 4, 'Charming hotel in Zurich', 'A charming hotel in Zurich blending modern and traditional Swiss hospitality, with beautiful surroundings.', 4, 'hotel_zurich1.jpg', '16:00:00', '10:00:00'),
                                                                                                                      ('Hotel Zurich 2', 5, 'Premium hotel in Zurich', 'A premium hotel in Zurich offering luxurious rooms, top-notch amenities, and exceptional service.', 5, 'hotel_zurich2.jpg', '14:00:00', '12:00:00'),
                                                                                                                      ('Hotel Zurich 3', 6, 'Affordable hotel in Zurich', 'An affordable hotel in Zurich with clean rooms, friendly staff, and a convenient location.', 3, 'hotel_zurich3.jpg', '15:00:00', '11:00:00');

-- Insert hotel rooms with updated column names
INSERT INTO hotel_room (name, description, cost_per_night, child_friendly, capacity, hotel_id, area) VALUES
                                                                                                         -- Hotel New York 1
                                                                                                         ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, 1, (20 + random() * 50)::numeric),
                                                                                                         ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, 1, (20 + random() * 50)::numeric),
                                                                                                         ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, false, 4, 1, (20 + random() * 50)::numeric),
                                                                                                         ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, 1, (20 + random() * 50)::numeric),
                                                                                                         -- Hotel New York 2
                                                                                                         ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, 2, (20 + random() * 50)::numeric),
                                                                                                         ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, 2, (20 + random() * 50)::numeric),
                                                                                                         ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, 2, (20 + random() * 50)::numeric),
                                                                                                         ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, 2, (20 + random() * 50)::numeric),
                                                                                                         -- Hotel New York 3
                                                                                                         ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, 3, (20 + random() * 50)::numeric),
                                                                                                         ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, false, 2, 3, (20 + random() * 50)::numeric),
                                                                                                         ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, 3, (20 + random() * 50)::numeric),
                                                                                                         ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, 3, (20 + random() * 50)::numeric),
                                                                                                         -- Hotel Zurich 1
                                                                                                         ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, 4, (20 + random() * 50)::numeric),
                                                                                                         ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, 4, (20 + random() * 50)::numeric),
                                                                                                         ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, 4, (20 + random() * 50)::numeric),
                                                                                                         ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, 4, (20 + random() * 50)::numeric),
                                                                                                         -- Hotel Zurich 2
                                                                                                         ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, 5, (20 + random() * 50)::numeric),
                                                                                                         ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, 5, (20 + random() * 50)::numeric),
                                                                                                         ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, 5, (20 + random() * 50)::numeric),
                                                                                                         ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, 5, (20 + random() * 50)::numeric),
                                                                                                         -- Hotel Zurich 3
                                                                                                         ('Deluxe Room', 'A luxurious room with a king-sized bed, mini-bar, and a stunning city view. Includes free Wi-Fi and breakfast.', 50.00, true, 2, 6, (20 + random() * 50)::numeric),
                                                                                                         ('Standard Room', 'A comfortable room with essential amenities, a queen-sized bed, and a cozy atmosphere. Includes free Wi-Fi.', 30.00, true, 2, 6, (20 + random() * 50)::numeric),
                                                                                                         ('Suite', 'A spacious suite with a separate living area, premium amenities, and a beautiful city view. Includes free Wi-Fi and breakfast.', 40.00, true, 4, 6, (20 + random() * 50)::numeric),
                                                                                                         ('Family Room', 'A large room suitable for families, with multiple beds and child-friendly amenities. Includes free Wi-Fi.', 20.00, true, 4, 6, (20 + random() * 50)::numeric);

-- Insert hotel room images
INSERT INTO hotel_room_image (hotel_room_id, image_url) VALUES
                                                            -- Hotel New York 1
                                                            (1, 'deluxe_room_ny1.jpg'),
                                                            (2, 'standard_room_ny1.jpg'),
                                                            (3, 'suite_ny1.jpg'),
                                                            (4, 'family_room_ny1.jpg'),
                                                            -- Hotel New York 2
                                                            (5, 'deluxe_room_ny2.jpg'),
                                                            (6, 'standard_room_ny2.jpg'),
                                                            (7, 'suite_ny2.jpg'),
                                                            (8, 'family_room_ny2.jpg'),
                                                            -- Hotel New York 3
                                                            (9, 'deluxe_room_ny3.jpg'),
                                                            (10, 'standard_room_ny3.jpg'),
                                                            (11, 'suite_ny3.jpg'),
                                                            (12, 'family_room_ny3.jpg'),
                                                            -- Hotel Zurich 1
                                                            (13, 'deluxe_room_zurich1.jpg'),
                                                            (14, 'standard_room_zurich1.jpg'),
                                                            (15, 'suite_zurich1.jpg'),
                                                            (16, 'family_room_zurich1.jpg'),
                                                            -- Hotel Zurich 2
                                                            (17, 'deluxe_room_zurich2.jpg'),
                                                            (18, 'standard_room_zurich2.jpg'),
                                                            (19, 'suite_zurich2.jpg'),
                                                            (20, 'family_room_zurich2.jpg'),
                                                            -- Hotel Zurich 3
                                                            (21, 'deluxe_room_zurich3.jpg'),
                                                            (22, 'standard_room_zurich3.jpg'),
                                                            (23, 'suite_zurich3.jpg'),
                                                            (24, 'family_room_zurich3.jpg');

-- Insert room facilities
INSERT INTO facility (name) VALUES
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

-- Insert room facility links
INSERT INTO room_facility_link (hotel_room_id, facility_id)
SELECT
    hr.id,
    rf.id
FROM
    hotel_room hr
        JOIN facility rf ON 1=1
WHERE
    (hr.name LIKE '%Deluxe%' OR hr.name LIKE '%Suite%')
  AND rf.name IN ('Free WiFi', 'Shower', 'Air Conditioning', 'Flat-Screen TV', 'Mini Bar', 'Room Service', 'Coffee Maker', 'Safe', 'Hair Dryer', 'Iron and Ironing Board', 'Bathrobe and Slippers', 'Complimentary Toiletries', 'Desk', 'Seating Area', 'Soundproofing', 'Balcony', 'City View', 'Spa Bath', 'Hot Tub')
UNION ALL
SELECT
    hr.id,
    rf.id
FROM
    hotel_room hr
        JOIN facility rf ON 1=1
WHERE
    hr.name LIKE '%Standard%'
  AND rf.name IN ('Free WiFi', 'Shower', 'Air Conditioning', 'Flat-Screen TV', 'Coffee Maker', 'Safe', 'Hair Dryer', 'Desk', 'Seating Area', 'Soundproofing', 'Sauna')
UNION ALL
SELECT
    hr.id,
    rf.id
FROM
    hotel_room hr
        JOIN facility rf ON 1=1
WHERE
    hr.name LIKE '%Family%'
  AND rf.name IN ('Free WiFi', 'Shower', 'Air Conditioning', 'Flat-Screen TV', 'Coffee Maker', 'Safe', 'Hair Dryer', 'Desk', 'Seating Area', 'Soundproofing', 'Kitchenette');

-- Insert room highlights
INSERT INTO highlight (name, image_url) VALUES
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

-- Insert room highlight links
INSERT INTO room_highlight_link (hotel_room_id, highlight_id)
SELECT
    hr.id,
    rh.id
FROM
    hotel_room hr
        JOIN highlight rh ON 1=1
WHERE
    (hr.name LIKE '%Deluxe%' OR hr.name LIKE '%Suite%')
  AND rh.name IN ('King-sized Bed', 'Beautiful View', 'Antique Furniture', 'Modern Amenities', 'Private Balcony', 'Spacious Living Area', 'Luxurious Bathroom', 'Stunning City View', 'Complimentary Breakfast')
UNION ALL
SELECT
    hr.id,
    rh.id
FROM
    hotel_room hr
        JOIN highlight rh ON 1=1
WHERE
    hr.name LIKE '%Standard%'
  AND rh.name IN ('Queen-sized Bed', 'Comfortable Bed', 'Work Desk', 'Free Wi-Fi', 'Ergonomic Chair')
UNION ALL
SELECT
    hr.id,
    rh.id
FROM
    hotel_room hr
        JOIN highlight rh ON 1=1
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

-- Insert hotel highlight links
INSERT INTO hotel_highlight_link (hotel_id, hotel_highlight_id)
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

-- Insert new users with hashed passwords
INSERT INTO app_user (username, email, password_hash, phone, birth_date, gender, avatar_url, citizenship_country_id, residence_address_id, role) VALUES
                                                                                                                                                     -- password: password123
                                                                                                                                                     ('john_doe', 'john.doe@example.com', '$2a$10$LZVUP317ZxapsUy/96hqWOze6cRMOQUUReJq3Xg.PjEoUJTfLI2Pm', '123-456-7890', '1994-10-20', 'MALE', 'avatar_john.jpg', 1, 7, 'ROLE_USER'),
                                                                                                                                                     -- password: qwerty
                                                                                                                                                     ('jane_smith', 'jane.smith@example.com', '$2a$10$7FVWIPdfieQcAivdeoYY3eD55MCRUuPNnjkqnxDOGO7MWl5PnhMDm', '098-765-4321', '2010-06-10', 'MALE', 'avatar_jane.jpg', 2, 8, 'ROLE_USER'),
                                                                                                                                                     -- password: admin_pass
                                                                                                                                                     ('admin_user', 'admin@example.com', '$2a$10$SNsWC4N2oCuh4fmBxCqeaerDVbxHIZ.C.cwXaFoirp7LeVMXx68ny', '555-555-5555', '2001-01-25', 'FEMALE', 'avatar_admin.jpg', 1, 9, 'ROLE_ADMIN');

-- Insert email verifications
INSERT INTO email_verification (token, is_verified, app_user_id, expires_at) VALUES
                                                                                 ('123456', true, 1, '2024-12-31 23:59:59'),
                                                                                 ('654321', false, 2, '2024-12-31 23:59:59'),
                                                                                 ('987654', true, 3, '2024-12-31 23:59:59');

-- Insert passports
INSERT INTO passport (first_name, last_name, passport_number, expiration_date, issuing_country_id, app_user_id) VALUES
                                                                                                                    ('John', 'Doe', 'P1234567', '2030-12-31', 1, 1),
                                                                                                                    ('Admin', 'User', 'P1122334', '2021-10-31', 1, 3);

-- Insert reviews from the first two users
INSERT INTO review (rate, content, app_user_id, hotel_id, created_at, pros, cons) VALUES
                                                                                      (9.5, 'Great hotel with excellent service and amenities.', 1, 1, '2023-02-15 10:30:00', NULL, NULL),
                                                                                      (8.0, 'Very comfortable stay, will visit again.', 1, 2, '2023-06-01 14:00:00', NULL, NULL),
                                                                                      (7.5, 'Good value for money, but could improve cleanliness.', 2, 3, '2023-10-15 18:45:00', NULL, NULL),
                                                                                      (10.0, 'Absolutely loved the experience, highly recommend!', 2, 4, '2024-03-01 09:15:00', NULL, NULL);

-- Insert bookings with updated column names
INSERT INTO booking (app_user_id, hotel_room_id, guest_country_id, status_id, check_in, check_out, first_name, last_name, email, phone) VALUES
                                                                                                                                            -- Bookings for Hotel New York 1
                                                                                                                                            (1, 1, 1, 1, '2025-12-01', '2025-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            (1, 2, 1, 1, '2025-12-01', '2025-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            (1, 3, 1, 1, '2025-12-01', '2025-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            (1, 4, 1, 1, '2025-12-01', '2025-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            (1, 2, 1, 1, '2024-12-01', '2024-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            (1, 3, 1, 1, '2024-12-01', '2024-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            (1, 4, 1, 1, '2024-12-01', '2024-12-10', 'John', 'Doe', 'john.doe@example.com', '123-456-7890'),
                                                                                                                                            -- Additional bookings for other hotels
                                                                                                                                            (2, 5, 1, 1, '2024-12-15', '2024-12-20', 'Jane', 'Smith', 'jane.smith@example.com', '098-765-4321'),
                                                                                                                                            (2, 6, 1, 1, '2024-12-15', '2024-12-20', 'Jane', 'Smith', 'jane.smith@example.com', '098-765-4321'),
                                                                                                                                            (2, 7, 1, 1, '2024-12-15', '2024-12-20', 'Jane', 'Smith', 'jane.smith@example.com', '098-765-4321'),
                                                                                                                                            (2, 8, 1, 1, '2024-12-15', '2024-12-20', 'Jane', 'Smith', 'jane.smith@example.com', '098-765-4321');

-- Insert recently viewed hotels for all users
INSERT INTO recently_viewed_hotel (app_user_id, hotel_id, viewed_at) VALUES
                                                                         (1, 1, '2024-01-01 10:00:00'),
                                                                         (1, 2, '2024-01-02 11:00:00'),
                                                                         (1, 3, '2024-01-03 12:00:00'),
                                                                         (2, 1, '2024-01-01 10:00:00'),
                                                                         (2, 4, '2024-01-02 11:00:00'),
                                                                         (2, 5, '2024-01-03 12:00:00'),
                                                                         (3, 2, '2024-01-01 10:00:00'),
                                                                         (3, 3, '2024-01-02 11:00:00'),
                                                                         (3, 6, '2024-01-03 12:00:00');

-- Insert favorites for all users
INSERT INTO favorites (app_user_id, hotel_id) VALUES
                                                  (1, 1),
                                                  (1, 2),
                                                  (1, 3),
                                                  (2, 1),
                                                  (2, 4),
                                                  (2, 5),
                                                  (3, 2),
                                                  (3, 3),
                                                  (3, 6);

-- Insert bed types
INSERT INTO bed_type (bed_type) VALUES
                                    ('SINGLE'),
                                    ('DOUBLE');

-- Insert room bed links
INSERT INTO room_bed_link (hotel_room_id, bed_type_id) VALUES
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
                                                           (24, 2);

INSERT INTO booking_payment_option (option) VALUES
                                                ('CARD'),
                                                ('CASH');

INSERT INTO payment_status (status) VALUES
                                        ('COMPLETED'),
                                        ('ON_CHECK_IN'),
                                        ('REFUNDED');

-- Add "CARD" as a payment option for each hotel
INSERT INTO hotel_booking_payment_option_link (hotel_id, booking_payment_option_id)
SELECT id, (SELECT id FROM booking_payment_option WHERE option = 'CARD')
FROM hotel;

-- Add "CASH" as a payment option for every second hotel
INSERT INTO hotel_booking_payment_option_link (hotel_id, booking_payment_option_id)
SELECT id, (SELECT id FROM booking_payment_option WHERE option = 'CASH')
FROM hotel
WHERE id % 2 = 0;

-- Insert cancellation rules for every second hotel (even hotel IDs) and update the hotel table in one go
WITH cancel_rules AS (
    INSERT INTO booking_cancel_rule (appeal_if_cancelled_before_hours, cancel_fee, hotel_id)
        SELECT 24, 50.00, id
        FROM hotel
        WHERE id % 2 = 0
        RETURNING id AS cancel_rule_id, hotel_id
)
UPDATE hotel h
SET booking_cancel_rule_id = cr.cancel_rule_id
FROM cancel_rules cr
WHERE h.id = cr.hotel_id;

INSERT INTO bank_card (card_number, card_holder_name, expiration_date, cvv, app_user_id)
    VALUES ('1234567890123456', 'John Doe', '12/25', '123', 1);