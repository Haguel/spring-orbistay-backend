package dev.haguel.orbistay.util;

import java.util.stream.Stream;

public class EndPoints {
    public static class AppUsers {
        private static final String BASE_ENDPOINT = "/appUsers";
        public static final String GET_CURRENT_APP_USER = BASE_ENDPOINT + "/me";
        public static final String EDIT_CURRENT_APP_USER = BASE_ENDPOINT + "/me";

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    GET_CURRENT_APP_USER,
                    EDIT_CURRENT_APP_USER,
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

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    SIGN_UP,
                    SIGN_IN,
                    REFRESH_TOKENS,
                    REFRESH_ACCESS_TOKEN,
                    LOG_OUT
            };
        }

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    CHANGE_PASSWORD,
            };
        }
    }

    public static class Booking {
        private static final String BASE_ENDPOINT = "/booking";
        public static final String BOOK_HOTEL_ROOM = BASE_ENDPOINT + "/book";

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    BOOK_HOTEL_ROOM,
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
        public static final String WRITE_REVIEW = BASE_ENDPOINT + "/review";

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    GET_FILTERED_HOTELS,
                    GET_HOTEL + "/*",
                    GET_FILTERED_HOTEL_ROOMS,
                    GET_HOTEL_ROOM + "/*",
                    GET_POPULAR_HOTELS,
            };
        }

        public static String[] getAuthorizedEndpoints() {
            return new String[] {
                    WRITE_REVIEW,
            };
        }
    }

    public static class Destinations {
        private static final String BASE_ENDPOINT = "/destinations";
        public static final String GET_POPULAR_DESTINATIONS = BASE_ENDPOINT + "/popular";

        public static String[] getUnauthorizedEndpoints() {
            return new String[] {
                    GET_POPULAR_DESTINATIONS,
            };
        }
    }

    public static String[] getUnauthorizedEndpoints() {
        return Stream.of(
                Auth.getUnauthorizedEndpoints(),
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
                Hotels.getAuthorizedEndpoints()
        )
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }
}
