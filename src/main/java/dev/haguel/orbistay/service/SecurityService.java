package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.exception.InvalidJwtTokenException;
import dev.haguel.orbistay.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {
    private final JwtService jwtService;
    private final AppUserService appUserService;

    public AppUser getAppUserFromAuthorizationHeader(String authorizationHeader) throws InvalidJwtTokenException {
        String jwtToken = SecurityUtil.getTokenFromAuthorizationHeader(authorizationHeader);
        String appUserEmail = jwtService.getAccessClaims(jwtToken).getSubject();

        if(appUserEmail == null) {
            throw new InvalidJwtTokenException("Access token doesn't contain user email");
        }

        AppUser appUser = appUserService.findByEmail(appUserEmail);

        if(appUser == null) {
            throw new InvalidJwtTokenException("App user couldn't be found in database by provided email");
        }

        return appUser;
    }
}
