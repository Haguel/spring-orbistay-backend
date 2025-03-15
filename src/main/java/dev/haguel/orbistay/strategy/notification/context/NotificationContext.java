package dev.haguel.orbistay.strategy.notification.context;

import dev.haguel.orbistay.factory.EmailMessageFactory;
import dev.haguel.orbistay.factory.MessageFactory;
import dev.haguel.orbistay.strategy.notification.EmailNotificationStrategy;
import dev.haguel.orbistay.strategy.notification.NotificationStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Getter
@Slf4j
@RequiredArgsConstructor
public class NotificationContext {
    private NotificationStrategy strategy;
    private MessageFactory messageFactory;

    private final EmailNotificationStrategy emailNotificationStrategy;

    @Async
    public void notifyUser(String receiver, String subject, String content) {
        if (strategy == null) {
            throw new RuntimeException("Notification strategy not set");
        }

        strategy.sendNotification(receiver, subject, content);
        log.info("Message successfully sent to " + receiver);
    }

    public void setNotificationType(NotificationType notificationType) {
        if(notificationType == NotificationType.EMAIL) {
            this.strategy = emailNotificationStrategy;
            this.messageFactory = new EmailMessageFactory();
        }
    }
}
