package dev.haguel.orbistay.event.listener;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.event.AppUserSignUpEvent;
import dev.haguel.orbistay.strategy.notification.context.NotificationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppUserSignUpEventListener {
    private final NotificationContext notificationContext;

    @EventListener
    public void handleUserSignUpEvent(AppUserSignUpEvent event) {
        AppUser appUser = event.getAppUser();
        String message = notificationContext.getMessageFactory().getVerificationMessage(appUser);
        notificationContext.notifyUser(appUser.getEmail(), "Orbistay Authentication", message);
    }
}
