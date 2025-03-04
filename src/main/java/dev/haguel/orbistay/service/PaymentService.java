package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.request.BookingPaymentRequestDTO;
import dev.haguel.orbistay.entity.Booking;
import dev.haguel.orbistay.entity.BookingPaymentOption;
import dev.haguel.orbistay.entity.Payment;
import dev.haguel.orbistay.exception.InvalidPaymentException;
import dev.haguel.orbistay.repository.PaymentRepository;
import dev.haguel.orbistay.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final BookingService bookingService;

    private Payment save(Payment payment) {
        payment = paymentRepository.save(payment);

        log.info("Payment saved to db");
        return payment;
    }

    @Transactional
    private Payment createCompletePayment(BookingPaymentRequestDTO bookingPaymentRequestDTO, Booking booking) {
        Payment payment;
        if(bookingPaymentRequestDTO.getPaymentMethod().equals("CASH")) {
            payment = Payment.builder()
                    .amount(bookingPaymentRequestDTO.getAmount())
                    .paymentMethod(bookingPaymentRequestDTO.getPaymentMethod())
                    .currency(bookingPaymentRequestDTO.getCurrency())
                    .paymentStatus(paymentStatusRepository.findOnCheckInStatus())
                    .booking(booking)
                    .build();
        } else {
            payment = Payment.builder()
                    .amount(bookingPaymentRequestDTO.getAmount())
                    .paymentMethod(bookingPaymentRequestDTO.getPaymentMethod())
                    .currency(bookingPaymentRequestDTO.getCurrency())
                    .transactionId(UUID.randomUUID().toString())
                    .paymentStatus(paymentStatusRepository.findCompletedStatus())
                    .booking(booking)
                    .build();
        }

        return save(payment);
    }

    @Transactional
    public void payBooking(BookingPaymentRequestDTO bookingPaymentRequestDTO) {
        Booking booking = bookingService.findById(bookingPaymentRequestDTO.getBookingId());

        if(booking.getPayment() != null) {
            log.warn("Booking already paid");
            throw new InvalidPaymentException("Booking already paid");
        }

        List<String> hotelBookingPaymentOptions = booking.getHotelRoom().getHotel().getBookingPaymentOptions()
                .stream().map(BookingPaymentOption::getOption).toList();
        if(!hotelBookingPaymentOptions.contains(bookingPaymentRequestDTO.getPaymentMethod())) {
            log.warn("Payment method {} is not available for this hotel", bookingPaymentRequestDTO.getPaymentMethod());
            throw new InvalidPaymentException("Payment method is not available for this hotel");
        }

        LocalDate checkInDate = LocalDate.parse(booking.getCheckIn().format(DateTimeFormatter.ISO_LOCAL_DATE));
        LocalDate checkOutDate = LocalDate.parse(booking.getCheckOut().format(DateTimeFormatter.ISO_LOCAL_DATE));
        int nights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        Double totalCost = booking.getHotelRoom().getCostPerNight() * nights;
        Double amount = bookingPaymentRequestDTO.getAmount();
        if(!Objects.equals(amount, totalCost)) {
            log.warn("Payment amount {} is not equal to the booking total price {}", amount, totalCost);
            throw new InvalidPaymentException("Payment amount is not equal to hotel room price");
        }

        Payment payment = createCompletePayment(bookingPaymentRequestDTO, booking);

        bookingService.setCompletedPayment(booking, payment);
    }
}
