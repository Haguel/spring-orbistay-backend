-- Migration to remove old reviews and users, then add new users with varied reviews

-- Step 1: Remove all reviews associated with users created in the previous migration
-- Identify the users created in the previous migration (those with email ending in '@example.com')
DO $$
    DECLARE
        user_ids_to_delete BIGINT[];
    BEGIN
        -- Collect IDs of users with emails ending in '@example.com'
        SELECT ARRAY(
                       SELECT id
                       FROM app_user
                       WHERE email LIKE '%@example.com'
               ) INTO user_ids_to_delete;

        -- Delete all reviews associated with these users
        DELETE FROM review
        WHERE app_user_id = ANY(user_ids_to_delete);

        -- Delete the users
        DELETE FROM app_user
        WHERE id = ANY(user_ids_to_delete);
    END;
$$;

-- Insert new users with realistic usernames, countries, and avatars
INSERT INTO app_user (username, password_hash, email, birth_date, gender, avatar_url, citizenship_country_id, role)
VALUES
    ('JeanDupont', 'hashed_password_1', 'jean.dupont@email.com', '1990-05-15', 'MALE', 'https://orbistayblob.blob.core.windows.net/orbistay-container/изображение_2025-03-14_151107521.png', (SELECT id FROM country WHERE code = 'FR'), 'ROLE_USER'),
    ('MariaSchneider', 'hashed_password_2', 'maria.schneider@email.com', '1985-09-22', 'FEMALE', 'https://orbistayblob.blob.core.windows.net/orbistay-container/изображение_2025-03-14_151324455.png', (SELECT id FROM country WHERE code = 'DE'), 'ROLE_USER'),
    ('JohnSmith', 'hashed_password_3', 'john.smith@email.com', '1992-03-10', 'MALE', 'https://orbistayblob.blob.core.windows.net/orbistay-container/изображение_2025-03-14_151156089.png', (SELECT id FROM country WHERE code = 'US'), 'ROLE_USER'),
    ('EmmaWilson', 'hashed_password_4', 'emma.wilson@email.com', '1988-12-01', 'FEMALE', 'https://orbistayblob.blob.core.windows.net/orbistay-container/изображение_2025-03-14_151353377.png', (SELECT id FROM country WHERE code = 'GB'), 'ROLE_USER'),
    ('LucaRossi', 'hashed_password_5', 'luca.rossi@email.com', '1995-07-18', 'MALE', 'https://orbistayblob.blob.core.windows.net/orbistay-container/изображение_2025-03-14_151245870.png', (SELECT id FROM country WHERE code = 'IT'), 'ROLE_USER')
ON CONFLICT (email) DO NOTHING;

-- Step 3: Insert reviews for each new user for every hotel with varied content
INSERT INTO review (app_user_id, hotel_id, rate, content, pros, cons, created_at)
SELECT
    u.id AS app_user_id,
    h.id AS hotel_id,
    FLOOR(RANDOM() * 6 + 5)::NUMERIC(3, 1) AS rate, -- Random rate between 5 and 10
    CASE FLOOR(RANDOM() * 6)::INT
        WHEN 0 THEN 'Amazing stay at ' || h.name || '! The staff went above and beyond to make us feel welcome.'
        WHEN 1 THEN 'Really enjoyed my time at ' || h.name || '. The rooms were spacious and beautifully decorated.'
        WHEN 2 THEN 'A decent experience at ' || h.name || ', though there were a few hiccups during check-in.'
        WHEN 3 THEN 'Fantastic location at ' || h.name || '! Perfect for exploring the city.'
        WHEN 4 THEN 'I had a mixed experience at ' || h.name || '. The facilities were great, but the service could improve.'
        WHEN 5 THEN 'Wonderful hotel! ' || h.name || ' exceeded my expectations with its charm and hospitality.'
        END AS content,
    CASE FLOOR(RANDOM() * 5)::INT
        WHEN 0 THEN 'Friendly staff, clean rooms, great breakfast.'
        WHEN 1 THEN 'Amazing views, comfortable beds, quiet atmosphere.'
        WHEN 2 THEN 'Central location, modern amenities, good Wi-Fi.'
        WHEN 3 THEN 'Spacious bathroom, excellent dining options, welcoming vibe.'
        WHEN 4 THEN NULL
        END AS pros,
    CASE FLOOR(RANDOM() * 5)::INT
        WHEN 0 THEN 'Limited parking, slow check-in process.'
        WHEN 1 THEN 'Noisy at night, small elevator.'
        WHEN 2 THEN 'Expensive dining, Wi-Fi dropped occasionally.'
        WHEN 3 THEN 'Room service was slow, thin walls.'
        WHEN 4 THEN NULL
        END AS cons,
    CURRENT_TIMESTAMP - INTERVAL '1 day' * FLOOR(RANDOM() * 30) AS created_at -- Reviews from the past 30 days
FROM app_user u
         CROSS JOIN hotel h
WHERE u.username IN ('JeanDupont', 'MariaSchneider', 'JohnSmith', 'EmmaWilson', 'LucaRossi');