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

    public void sendVerificationEmail(AppUser appUser) throws EmailSendingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            String verificationLink = frontendHost + "/verify-email?token=" + appUser.getEmailVerification().getToken();
            message.setFrom(new InternetAddress(this.username));
            message.setRecipients(MimeMessage.RecipientType.TO, appUser.getEmail());
            message.setSubject("Orbistay Authentication");

            String htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Verify Your Email – Orbistay</title>
                    <style>
                      body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f4f4f4;
                        padding: 20px;
                      }
                      .email-wrapper {
                        background: #ffffff;
                        border-radius: 10px;
                        padding: 30px;
                        margin: 20px auto;
                        width: 100%%;
                        max-width: 600px;
                        box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
                      }
                      h1 {
                        color: #2c3e50;
                        font-size: 24px;
                        margin-bottom: 20px;
                      }
                      p {
                        font-size: 16px;
                        color: #555;
                      }
                      a.button {
                        display: inline-block;
                        padding: 12px 24px;
                        font-size: 16px;
                        font-weight: bold;
                        color: #ffffff;
                        background-color: #3498db;
                        border-radius: 5px;
                        text-decoration: none;
                        margin-top: 20px;
                      }
                      a.button:hover {
                        background-color: #2980b9;
                      }
                      .footer {
                        font-size: 12px;
                        color: #888;
                        margin-top: 30px;
                        text-align: center;
                      }
                      .footer a {
                        color: #888;
                        text-decoration: none;
                      }
                      .footer a:hover {
                        text-decoration: underline;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="email-wrapper">
                      <h1>Welcome to Orbistay! 🌟</h1>
                      <p>Hi <strong>%s</strong>,</p>
                      <p>Thank you for signing up for Orbistay! Please verify your email address by clicking the button below:</p>
                      <a href="%s" class="button">Verify Your Email</a>
                      <p>If the button doesn't work, you can also verify your account by copying and pasting the following link into your browser:</p>
                      <p><a href="%s">%s</a></p>
                      <p>This link will expire in 24 hours.</p>
                      <p>If you didn’t sign up for Orbistay, please ignore this email.</p>
                      <div class="footer">
                        <p>Thank you for choosing Orbistay! 🌍</p>
                        <p>&copy; %d Orbistay. All rights reserved.</p>
                        <p><a href="%s">Visit our website</a> | <a href="%s/support">Support</a></p>
                      </div>
                    </div>
                  </body>
                </html>
            """;

            String formattedHtmlContent = String.format(htmlContent,
                    appUser.getUsername(),
                    verificationLink, verificationLink, verificationLink,
                    Year.now().getValue(),
                    frontendHost, frontendHost
            );

            message.setContent(formattedHtmlContent, "text/html; charset=utf-8");
            mailSender.send(message);
            log.info("Verification email to {} has been sent", appUser.getEmail());
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send verification email");
        }
    }
}
