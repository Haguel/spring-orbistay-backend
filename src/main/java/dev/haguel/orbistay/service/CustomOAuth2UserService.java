package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.enumeration.Role;
import dev.haguel.orbistay.exception.AppUserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final AppUserService appUserService;
    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        log.info("OAuth2 service started");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(request);

        log.info("OAuth2User loaded");
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        AppUser appUser;
        try {
            appUser = appUserService.findByEmail(email);
            log.warn("should not be here");
        } catch (AppUserNotFoundException exception) {
            log.info("appUser not found");
            appUser = null;
        } catch (Exception e) {
            log.error("wtf", e.getMessage());
            appUser = null;
        }

        if (appUser == null) {
            log.info("Creating new app user");
            appUser = new AppUser();
            appUser.setEmail(email);
            appUser.setAvatarUrl((String) attributes.get("picture"));
            appUser.setUsername((String) attributes.get("name"));
            appUser.setRole(Role.ROLE_USER);
            appUser = appUserService.save(appUser);

            appUser.setEmailVerification(emailVerificationService.createCompletedVerificationForAppUser(appUser));
            appUser = appUserService.save(appUser);
        }

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),
                attributes,
                "email"
        );
    }
}