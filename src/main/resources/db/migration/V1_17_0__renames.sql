-- Rename tables
ALTER TABLE place_of_birth RENAME TO birthplace;
ALTER TABLE room_facility RENAME TO facility;
ALTER TABLE room_bed RENAME TO bed_type;
ALTER TABLE room_highlight RENAME TO highlight;
ALTER TABLE hotel_room_room_facility RENAME TO room_facility_link;
ALTER TABLE hotel_room_room_bed RENAME TO room_bed_link;
ALTER TABLE hotel_room_room_highlight RENAME TO room_highlight_link;
ALTER TABLE hotel_hotel_highlight RENAME TO hotel_highlight_link;

-- Fix data type inconsistency in room_bed_link (originally hotel_room_room_bed)
ALTER TABLE room_bed_link ALTER COLUMN hotel_room_id TYPE BIGINT;
ALTER TABLE room_bed_link RENAME COLUMN room_bed_id TO bed_type_id;

ALTER TABLE room_facility_link RENAME COLUMN room_facility_id TO facility_id;

ALTER TABLE room_highlight_link RENAME COLUMN room_highlight_id TO highlight_id;

-- Rename columns in 'app_user'
ALTER TABLE app_user RENAME COLUMN citizenship_id TO citizenship_country_id;
ALTER TABLE app_user RENAME COLUMN residency_id TO residence_address_id;

-- Rename columns in 'passport'
ALTER TABLE passport RENAME COLUMN country_of_issuance_id TO issuing_country_id;

-- Rename columns in 'hotel_room'
ALTER TABLE hotel_room RENAME COLUMN metering TO area;
ALTER TABLE hotel_room RENAME COLUMN is_children_friendly TO child_friendly;

-- Rename columns in 'booking'
ALTER TABLE booking RENAME COLUMN phone_number TO phone;
ALTER TABLE booking RENAME COLUMN booking_status_id TO status_id;
ALTER TABLE booking RENAME COLUMN country_id TO guest_country_id;

-- Rename columns in 'review'
ALTER TABLE review RENAME COLUMN good_sides TO pros;
ALTER TABLE review RENAME COLUMN bad_sides TO cons;