package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.EmailVerification;
import dev.haguel.orbistay.exception.EmailSendingException;
import dev.haguel.orbistay.exception.EmailVerificationNotFoundException;
import dev.haguel.orbistay.repository.EmailVerificationRepository;
import dev.haguel.orbistay.util.Generator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String username;
    @Value("${frontend.host}")
    private String frontendHost;
    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private String generateToken() {
        return Generator.generateRandomString(10);
    }

    public EmailVerification save(EmailVerification emailVerification) {
        emailVerification = emailVerificationRepository.save(emailVerification);

        log.info("Email verification saved");
        return emailVerification;
    }

    public void delete(EmailVerification emailVerification) {
        emailVerificationRepository.delete(emailVerification);

        log.info("Email verification deleted");
    }

    @Transactional(readOnly = true)
    public EmailVerification findByToken(String token) {
        EmailVerification emailVerification = emailVerificationRepository.findByToken(token).orElse(null);

        if (emailVerification == null) {
            log.warn("Email verification not found by token: {}", token);
            throw new EmailVerificationNotFoundException("Email verification not found");
        } else {
            log.info("Email verification found");
        }

        return emailVerification;
    }

    public EmailVerification createNeededVerificationForAppUser(AppUser appUser) {
        EmailVerification emailVerification = EmailVerification.builder()
                .token(UUID.randomUUID().toString())
                .appUser(appUser)
                .token(generateToken())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        return save(emailVerification);
    }

    public EmailVerification createCompletedVerificationForAppUser(AppUser appUser) {
        EmailVerification emailVerification = EmailVerification.builder()
                .token(UUID.randomUUID().toString())
                .appUser(appUser)
                .token(null)
                .isVerified(true)
                .build();

        return save(emailVerification);
    }

    public EmailVerification changeAndSaveEmailVerificationToVerified(EmailVerification emailVerification) {
        emailVerification.setToken(null);
        emailVerification.setVerified(true);

        return save(emailVerification);
    }

    public EmailVerification continueAndSaveVerification(EmailVerification emailVerification) {
        emailVerification.setToken(generateToken());
        emailVerification.setVerified(false);
        emailVerification.setExpiresAt(LocalDateTime.now().plusDays(1));

        return save(emailVerification);
    }
}
