package test_utils;

import dev.haguel.orbistay.dto.response.JwtResponseDTO;
import dev.haguel.orbistay.dto.request.SignInRequestDTO;
import dev.haguel.orbistay.util.EndPoints;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class SharedTestUtil {
    public static JwtResponseDTO signInAndGetTokens(String email, String password, WebTestClient webTestClient) {
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
                .expectBody(JwtResponseDTO.class)
                .returnResult()
                .getResponseBody();
    }

    public static JwtResponseDTO signInJohnDoeAndGetTokens(WebTestClient webTestClient) {
        return signInAndGetTokens(TestDataStorage.JOHN_DOE_EMAIL, TestDataStorage.JOHN_DOE_PASSWORD, webTestClient);
    }
}
