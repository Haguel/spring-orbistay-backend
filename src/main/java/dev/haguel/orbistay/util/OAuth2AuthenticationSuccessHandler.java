package dev.haguel.orbistay.util;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.service.AppUserService;
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

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AppUserService appUserService;

    @Value("${frontend.host}")
    private String frontendHost;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 handler started");
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        AppUser appUser = appUserService.findByEmail((String) defaultOAuth2User.getAttributes().get("email"));
        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        redisService.setValue(appUser.getEmail(), refreshToken);

        log.info("Successful OAuth2 authentication. Redirecting to frontend");
        String redirectUrl = frontendHost + "/authRedirect?accessToken=" + accessToken + "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrl);
    }
}

