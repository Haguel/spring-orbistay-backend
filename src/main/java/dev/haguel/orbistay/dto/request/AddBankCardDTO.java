package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "AddBankCardDTO", description = "Data Transfer Object for adding bank card")
public class AddBankCardDTO {
    @Schema(description = "Card number", example = "1234567890123456")
    @Size(min = 16, max = 16)
    @Pattern(regexp = "\\d{16}", message = "Invalid card number")
    private String cardNumber;

    @Schema(description = "Card expiration date", example = "12/23")
    @Size(min = 5, max = 5)
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiration date must be in the format MM/YY")
    private String expirationDate;

    @Schema(description = "Card CVV", example = "123")
    @Size(min = 3, max = 3)
    @Pattern(regexp = "\\d{3}", message = "Invalid CVV")
    private String cvv;

    @Schema(description = "Card holder name", example = "John Doe")
    private String cardHolderName;
}
