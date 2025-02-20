package dev.haguel.orbistay.util;

import java.util.stream.Stream;

public class EndPoints {
    public static class AppUsers {
        private static final String BASE_ENDPOINT = "/appUsers";
        public static final String GET_CURRENT_APP_USER = BASE_ENDPOINT + "/me";
        public static final String EDIT_CURRENT_APP_USER = BASE_ENDPOINT + "/me";
        public static final String UPLOAD_AVATAR = BASE_ENDPOINT + "/me/avatar";

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    GET_CURRENT_APP_USER,
                    EDIT_CURRENT_APP_USER,
                    UPLOAD_AVATAR,
            };
        }
    }

    public static class Auth {
        private static final String BASE_ENDPOINT = "/auth";
        public static final String SIGN_UP = BASE_ENDPOINT + "/signUp";
        public static final String SIGN_IN = BASE_ENDPOINT + "/signIn";
        public static final String LOG_OUT = BASE_ENDPOINT + "/logOut";
        public static final String CHANGE_PASSWORD = BASE_ENDPOINT + "/changePassword";
        public static final String REFRESH_TOKENS = BASE_ENDPOINT + "/refresh/tokens";
        public static final String REFRESH_ACCESS_TOKEN = BASE_ENDPOINT + "/refresh/tokens/access";
        public static final String VERIFY_EMAIL = BASE_ENDPOINT + "/email/verify";
        public static final String RESEND_EMAIL_VERIFICATION = BASE_ENDPOINT + "/email/resendVerification";
        public static final String REQUEST_RESET_PASSWORD = BASE_ENDPOINT + "/resetPassword/request";
        public static final String RESET_PASSWORD = BASE_ENDPOINT + "/resetPassword";

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    SIGN_UP,
                    SIGN_IN,
                    REFRESH_TOKENS,
                    REFRESH_ACCESS_TOKEN,
                    LOG_OUT,
                    VERIFY_EMAIL,
                    REQUEST_RESET_PASSWORD,
                    RESET_PASSWORD
            };
        }

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    CHANGE_PASSWORD,
                    RESEND_EMAIL_VERIFICATION
            };
        }
    }

    public static class OAuth2 {
        private static final String BASE_ENDPOINT = "/oauth2";

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    BASE_ENDPOINT
            };
        }
    }

    public static class Booking {
        private static final String BASE_ENDPOINT = "/bookings";
        public static final String BOOK_HOTEL_ROOM = BASE_ENDPOINT;
        public static final String GET_BOOKINGS = BASE_ENDPOINT + "/me";
        public static final String CANCEL_BOOKING = BASE_ENDPOINT;

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    BOOK_HOTEL_ROOM,
                    GET_BOOKINGS + "/*",
                    CANCEL_BOOKING + "/*",
            };
        }
    }

    public static class Favorites {
        private static final String BASE_ENDPOINT = "/favorites";
        public static final String ADD_TO_FAVORITES = BASE_ENDPOINT;
        public static final String GET_FAVORITES = BASE_ENDPOINT + "/me";
        public static final String REMOVE_FAVORITES = BASE_ENDPOINT;

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    ADD_TO_FAVORITES + "/*",
                    GET_FAVORITES,
                    REMOVE_FAVORITES + "/*",
            };
        }
    }

    public static class Hotels {
        private static final String BASE_ENDPOINT = "/hotels";
        public static final String GET_FILTERED_HOTELS = BASE_ENDPOINT + "/filter";
        public static final String GET_HOTEL = BASE_ENDPOINT;
        public static final String GET_FILTERED_HOTEL_ROOMS = BASE_ENDPOINT + "/rooms/filter";
        public static final String GET_HOTEL_ROOM = BASE_ENDPOINT + "/rooms";
        public static final String GET_POPULAR_HOTELS = BASE_ENDPOINT + "/popular";
        public static final String WRITE_HOTEL_REVIEW = BASE_ENDPOINT + "/reviews";
        public static final String GET_HOTEL_REVIEWS = BASE_ENDPOINT + "/reviews";
        public static final String REMOVE_HOTEL_REVIEW = BASE_ENDPOINT + "/reviews";

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    GET_FILTERED_HOTELS,
                    GET_HOTEL + "/*",
                    GET_FILTERED_HOTEL_ROOMS,
                    GET_HOTEL_ROOM + "/*",
                    GET_POPULAR_HOTELS,
                    GET_HOTEL_REVIEWS + "/*",
            };
        }

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    WRITE_HOTEL_REVIEW,
                    REMOVE_HOTEL_REVIEW + "/*",
            };
        }
    }

    public static class RecentlyViewedHotels {
        private static final String BASE_ENDPOINT = "/recentlyViewedHotels";
        public static final String GET_RECENTLY_VIEWED_HOTELS = BASE_ENDPOINT + "/me";
        public static final String ADD_TO_RECENTLY_VIEWED_HOTELS = BASE_ENDPOINT + "/me";

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    GET_RECENTLY_VIEWED_HOTELS,
                    ADD_TO_RECENTLY_VIEWED_HOTELS + "/*",
            };
        }
    }

    public static class Destinations {
        private static final String BASE_ENDPOINT = "/destinations";
        public static final String GET_POPULAR_DESTINATIONS = BASE_ENDPOINT + "/popular";
        public static final String GET_DESTINATIONS_SIMILAR_TO_TEXT = BASE_ENDPOINT + "/similar";

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    GET_POPULAR_DESTINATIONS,
                    GET_DESTINATIONS_SIMILAR_TO_TEXT,
            };
        }
    }

    public static String[] getUnauthorizedEndpoints() {
        return Stream.of(
                Auth.getUnauthorizedEndpoints(),
                OAuth2.getUnauthorizedEndpoints(),
                Hotels.getUnauthorizedEndpoints(),
                Destinations.getUnauthorizedEndpoints()
        )
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }

    public static String[] getAuthorizedEndpoints() {
        return Stream.of(
                AppUsers.getAuthorizedEndpoints(),
                Auth.getAuthorizedEndpoints(),
                Booking.getAuthorizedEndpoints(),
                Hotels.getAuthorizedEndpoints(),
                RecentlyViewedHotels.getAuthorizedEndpoints(),
                Favorites.getAuthorizedEndpoints()
        )
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }
}
