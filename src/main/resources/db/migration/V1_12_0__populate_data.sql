-- Insert new users and capture their IDs
WITH new_users AS (
    INSERT INTO app_user (username, email, role)
        SELECT
            substring(md5(random()::text) for 8) AS username,
            substring(md5(random()::text) for 8) || '@example.com' AS email,
            'ROLE_USER' AS role
        FROM generate_series(1, 20)
        RETURNING id, username
)

-- Insert one review per new user for each hotel
INSERT INTO review (app_user_id, hotel_id, rate, content)
SELECT
    u.id AS app_user_id,
    h.id AS hotel_id,
    floor(random() * 7 + 4) AS rate,
    'Review from ' || u.username AS content
FROM new_users u
         CROSS JOIN hotel h;