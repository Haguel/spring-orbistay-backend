package test_utils;

import dev.haguel.orbistay.dto.request.SignUpRequestDTO;
import dev.haguel.orbistay.dto.response.AccessTokenResponseDTO;
import dev.haguel.orbistay.dto.request.SignInRequestDTO;
import dev.haguel.orbistay.util.EndPoints;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class SharedTestUtil {
    public static AccessTokenResponseDTO signInAndGetAccessToken(String email, String password, WebTestClient webTestClient) {
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        return webTestClient.post()
                .uri(EndPoints.Auth.SIGN_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccessTokenResponseDTO.class)
                .returnResult()
                .getResponseBody();
    }

    public static String signInAndGetRefreshToken(String email, String password, WebTestClient webTestClient) {
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        return webTestClient.post()
                .uri(EndPoints.Auth.SIGN_IN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AccessTokenResponseDTO.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.SET_COOKIE)
                .split(";")[0]
                .split("=")[1];
    }

    public static AccessTokenResponseDTO signUpAndGetAccessToken(String username, String email, String password, WebTestClient webTestClient) {
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        return webTestClient.post()
                .uri(EndPoints.Auth.SIGN_UP)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUpRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccessTokenResponseDTO.class)
                .returnResult()
                .getResponseBody();
    }

    public static AccessTokenResponseDTO signInJohnDoeAndGetAccessToken(WebTestClient webTestClient) {
        return signInAndGetAccessToken(TestDataStorage.JOHN_DOE_EMAIL, TestDataStorage.JOHN_DOE_PASSWORD, webTestClient);
    }

    public static String signInJohnDoeAndGetRefreshToken(WebTestClient webTestClient) {
        return signInAndGetRefreshToken(TestDataStorage.JOHN_DOE_EMAIL, TestDataStorage.JOHN_DOE_PASSWORD, webTestClient);
    }
}
