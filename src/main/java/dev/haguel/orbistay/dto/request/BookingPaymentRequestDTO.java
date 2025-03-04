package dev.haguel.orbistay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BookingPaymentRequest", description = "Booking payment data transfer object")
public class BookingPaymentRequestDTO {
    @Schema(description = "Hotel ID", example = "1")
    @NotNull
    Long bookingId;

    @Schema(description = "Amount of money paid", example = "100.0")
    @NotNull
    Double amount;

    @Schema(description = "Payment method", example = "CASH")
    @NotNull
    @NotBlank
    String paymentMethod;

    @Schema(description = "Currency of the payment", example = "USD")
    @NotNull
    @NotBlank
    String currency;
}
