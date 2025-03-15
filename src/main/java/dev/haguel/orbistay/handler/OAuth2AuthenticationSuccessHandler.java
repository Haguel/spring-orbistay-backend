package dev.haguel.orbistay.handler;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.enumeration.Role;
import dev.haguel.orbistay.service.AppUserService;
import dev.haguel.orbistay.service.EmailService;
import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AppUserService appUserService;
    private final EmailService emailService;

    @Value("${frontend.host}")
    private String frontendHost;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 handler started");
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = (String) defaultOAuth2User.getAttributes().get("email");
        AppUser appUser = appUserService.findByEmail(email);

        if (appUser == null) {
            log.info("Creating new app user");
            appUser = new AppUser();
            appUser.setEmail(email);
            appUser.setAvatarUrl((String) defaultOAuth2User.getAttributes().get("picture"));
            appUser.setUsername((String) defaultOAuth2User.getAttributes().get("name"));
            appUser.setRole(Role.ROLE_USER);
            appUser = appUserService.save(appUser);
        }

        if(appUser.getEmailVerification() == null || !appUser.getEmailVerification().isVerified()) {
            appUser.setEmailVerification(emailService.createCompletedVerificationForAppUser(appUser));
            appUser = appUserService.save(appUser);
        }


        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        redisService.setAuthValue(appUser.getEmail(), refreshToken);

        log.info("Successful OAuth2 authentication. Redirecting to frontend");
        String redirectUrl = frontendHost + "/authRedirect?accessToken=" + accessToken + "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrl);
    }
}

