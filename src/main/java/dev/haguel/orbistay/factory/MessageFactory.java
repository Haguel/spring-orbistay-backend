package dev.haguel.orbistay.factory;

import dev.haguel.orbistay.entity.AppUser;

public interface MessageFactory {
    String getVerificationMessage(AppUser appUser);
    String getResetPasswordMessage(String token);
}
