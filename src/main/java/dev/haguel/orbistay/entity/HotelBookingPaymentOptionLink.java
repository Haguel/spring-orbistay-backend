package dev.haguel.orbistay.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"hotel", "bookingPaymentOption"})
@EqualsAndHashCode(exclude = {"hotel", "bookingPaymentOption"})
@Entity(name = "hotel_booking_payment_option_link")
public class HotelBookingPaymentOptionLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "booking_payment_option_id", nullable = false)
    private BookingPaymentOption bookingPaymentOption;
}
