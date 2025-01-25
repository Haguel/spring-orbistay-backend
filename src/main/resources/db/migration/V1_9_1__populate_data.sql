INSERT INTO hotel_image (hotel_id, image_url) VALUES
    (1, 'https://orbistayblob.blob.core.windows.net/hotels/cozy-ny-hotel-1.svg'),
    (1, 'https://orbistayblob.blob.core.windows.net/hotels/cozy-ny-hotel-2.svg'),
    (1, 'https://orbistayblob.blob.core.windows.net/hotels/cozy-ny-hotel-3.svg'),
    (1, 'https://orbistayblob.blob.core.windows.net/hotels/cozy-ny-hotel-4.svg'),
    (2, 'https://orbistayblob.blob.core.windows.net/hotels/luxury-ny-hotel-1.svg'),
    (2, 'https://orbistayblob.blob.core.windows.net/hotels/luxury-ny-hotel-2.svg'),
    (2, 'https://orbistayblob.blob.core.windows.net/hotels/luxury-ny-hotel-3.svg'),
    (2, 'https://orbistayblob.blob.core.windows.net/hotels/luxury-ny-hotel-4.svg'),
    (2, 'https://orbistayblob.blob.core.windows.net/hotels/luxury-ny-hotel-5.svg'),
    (3, 'https://orbistayblob.blob.core.windows.net/hotels/budget-ny-hotel-1.svg'),
    (3, 'https://orbistayblob.blob.core.windows.net/hotels/budget-ny-hotel-2.svg'),
    (3, 'https://orbistayblob.blob.core.windows.net/hotels/budget-ny-hotel-3.svg'),
    (3, 'https://orbistayblob.blob.core.windows.net/hotels/budget-ny-hotel-4.svg'),
    (3, 'https://orbistayblob.blob.core.windows.net/hotels/budget-ny-hotel-5.svg'),
    (4, 'https://orbistayblob.blob.core.windows.net/hotels/charmin-zurich-hotel-1.svg'),
    (4, 'https://orbistayblob.blob.core.windows.net/hotels/charmin-zurich-hotel-2.svg'),
    (4, 'https://orbistayblob.blob.core.windows.net/hotels/charmin-zurich-hotel-3.svg'),
    (4, 'https://orbistayblob.blob.core.windows.net/hotels/charmin-zurich-hotel-4.svg'),
    (4, 'https://orbistayblob.blob.core.windows.net/hotels/charmin-zurich-hotel-5.svg'),
    (5, 'https://orbistayblob.blob.core.windows.net/hotels/premium-zurich-hotel-1.svg'),
    (5, 'https://orbistayblob.blob.core.windows.net/hotels/premium-zurich-hotel-2.svg'),
    (5, 'https://orbistayblob.blob.core.windows.net/hotels/premium-zurich-hotel-3.svg'),
    (5, 'https://orbistayblob.blob.core.windows.net/hotels/premium-zurich-hotel-4.svg'),
    (5, 'https://orbistayblob.blob.core.windows.net/hotels/premium-zurich-hotel-5.svg'),
    (6, 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-1.svg'),
    (6, 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-2.svg'),
    (6, 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-3.svg'),
    (6, 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-4.svg'),
    (6, 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-5.svg'),
    (6, 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-6.svg');

UPDATE hotel SET main_image_url = 'https://orbistayblob.blob.core.windows.net/hotels/cozy-ny-hotel-main.svg' WHERE id = 1;
UPDATE hotel SET main_image_url = 'https://orbistayblob.blob.core.windows.net/hotels/luxury-ny-hotel-main.svg' WHERE id = 2;
UPDATE hotel SET main_image_url = 'https://orbistayblob.blob.core.windows.net/hotels/budget-ny-hotel-main.svg' WHERE id = 3;
UPDATE hotel SET main_image_url = 'https://orbistayblob.blob.core.windows.net/hotels/charmin-zurich-hotel-main.svg' WHERE id = 4;
UPDATE hotel SET main_image_url = 'https://orbistayblob.blob.core.windows.net/hotels/premium-zurich-hotel-main.svg' WHERE id = 5;
UPDATE hotel SET main_image_url = 'https://orbistayblob.blob.core.windows.net/hotels/affordable-zurich-hotel-main.svg' WHERE id = 6;
