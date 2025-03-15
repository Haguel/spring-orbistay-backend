package dev.haguel.orbistay.event;

import dev.haguel.orbistay.entity.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppUserSignUpEvent extends ApplicationEvent {
    private AppUser appUser;

    public AppUserSignUpEvent(Object source, AppUser appUser) {
        super(source);
        this.appUser = appUser;
    }
}
