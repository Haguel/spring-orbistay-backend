package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String cardNumber;

    @Schema(description = "Card expiration date", example = "12/23")
    @Size(min = 5, max = 5)
    private String expirationDate;

    @Schema(description = "Card CVV", example = "123")
    @Size(min = 3, max = 3)
    private String cvv;

    @Schema(description = "Card holder name", example = "John Doe")
    private String cardHolderName;
}
