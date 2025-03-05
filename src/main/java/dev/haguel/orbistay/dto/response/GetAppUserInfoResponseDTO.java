package dev.haguel.orbistay.dto.response;

import dev.haguel.orbistay.entity.*;
import dev.haguel.orbistay.entity.enumeration.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Get app user info", description = "Data Transfer Object for getting app user info")
public class GetAppUserInfoResponseDTO {
    @Schema(description = "The app user's ID", example = "1")
    private Long id;

    @Schema(description = "The app user's username", example = "JohnDoe")
    private String username;

    @Schema(description = "The app user's birth date", example = "2000-01-01")
    private LocalDate birthDate;

    @Schema(description = "Gender of the app user", implementation = Gender.class)
    private Gender gender;

    @Schema(description = "The app user's email", example = "example@gmail.com")
    private String email;

    @Schema(description = "The app user's phone", example = "+1234567890")
    private String phone;

    @Schema(description = "The app user's avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "The app user's citizenship", implementation = Country.class)
    private Country citizenship;

    @Schema(description = "The app user's residency", implementation = Address.class)
    private Address residency;

    @Schema(description = "The app user's passport", implementation = Passport.class)
    private Passport passport;

    @Schema(description = "The app user's email verification status", implementation = EmailVerification.class)
    private EmailVerification emailVerification;

    @Schema(description = "The app user's bank cards", implementation = BankCard.class)
    private List<BankCard> bankCards;
}
