package dev.haguel.orbistay.strategy.notification;

import dev.haguel.orbistay.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy {
    @Value("${spring.mail.username}")
    private String username;
    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(String receiver, String subject, String formattedHtmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(this.username));
            message.setRecipients(MimeMessage.RecipientType.TO, receiver);
            message.setSubject(subject);

            message.setContent(formattedHtmlContent, "text/html; charset=utf-8");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send verification email");
        }
    }
}
