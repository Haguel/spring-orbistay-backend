package dev.haguel.orbistay.strategy.notification;

public interface NotificationStrategy {
    void sendNotification(String receiver, String subject, String content);
}
