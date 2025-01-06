package dev.haguel.orbistay.service;

import dev.haguel.orbistay.dto.*;
import dev.haguel.orbistay.entity.AppUser;
import dev.haguel.orbistay.entity.enumeration.Role;
import dev.haguel.orbistay.exception.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserService appUserService;
    private final UserDetailsCustomService userDetailsCustomService;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDTO signUp(SignUpRequestDTO signUpRequestDTO)
            throws UniquenessViolationException {
        AppUser appUser = AppUser.builder()
                .username(signUpRequestDTO.getUsername())
                .email(signUpRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(signUpRequestDTO.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        try {
            appUser = appUserService.save(appUser);
        } catch (DataIntegrityViolationException exception) {
            if(exception.getMessage().contains("email")) {
                throw new UniquenessViolationException("Email already exists");
            }
            if(exception.getMessage().contains("username")) {
                throw new UniquenessViolationException("Username already exists");
            }
        }

        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        redisService.setValue(appUser.getEmail(), refreshToken);

        return new JwtResponseDTO(accessToken, refreshToken);
    }

    public JwtResponseDTO signIn(SignInRequestDTO signInRequestDTO)
            throws AppUserNotFoundException, IncorrectAuthDataException {
        String email = signInRequestDTO.getEmail();
        UserDetails appUser = userDetailsCustomService.loadUserByUsername(email);
        if(appUser == null) {
            throw new AppUserNotFoundException("User not found");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequestDTO.getEmail(),
                    signInRequestDTO.getPassword()
            ));
        } catch (AuthenticationException exception) {
            throw new IncorrectAuthDataException("Incorrect email or password");
        }

        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        redisService.setValue(email, refreshToken);

        return new JwtResponseDTO(accessToken, refreshToken);
    }

    public JwtResponseDTO getAccessToken(String refreshToken)
            throws AppUserNotFoundException, InvalidJwtTokenException {
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw new InvalidJwtTokenException("Invalid refresh token");
        }

        Claims claims = jwtService.getRefreshClaims(refreshToken);
        String email = claims.getSubject();
        String storedRefreshToken = redisService.getValue(email);

        if (storedRefreshToken == null) {
            log.error("Refresh token doesn't bind to any email in redis");
            throw new AppUserNotFoundException("User not found");
        }

        UserDetails userDetails = userDetailsCustomService.loadUserByUsername(email);
        String accessToken = jwtService.generateAccessToken(userDetails);

        return new JwtResponseDTO(accessToken, null);
    }

    public JwtResponseDTO refresh(String refreshToken)
            throws AppUserNotFoundException, InvalidJwtTokenException {
        if (jwtService.validateRefreshToken(refreshToken)) {
            Claims claims = jwtService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            String storedRefreshToken = redisService.getValue(email);

            if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
                final AppUser appUser = appUserService.findByEmail(email);
                if(appUser == null) {
                    throw new AppUserNotFoundException("User not found");
                }

                String accessToken = jwtService.generateAccessToken(appUser);
                String newRefreshToken = jwtService.generateRefreshToken(appUser);
                redisService.setValue(appUser.getEmail(), newRefreshToken);

                return new JwtResponseDTO(accessToken, newRefreshToken);
            }
        }

        throw new InvalidJwtTokenException("Invalid refresh token");
    }

    public void logOut(String refreshToken)
            throws InvalidJwtTokenException {
        if(jwtService.validateRefreshToken(refreshToken)) {
            Claims claims = jwtService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();

            if(!redisService.hasKey(email)) {
                log.error("Refresh token doesn't bind to any email in redis");
                throw new InvalidJwtTokenException("Invalid refresh token");
            }

            redisService.deleteValue(email);
        } else {
            throw new InvalidJwtTokenException("Invalid refresh token");
        }
    }

    public void changePassword(AppUser appUser, ChangePasswordRequestDTO changePasswordRequestDTO)
            throws IncorrectPasswordException {
        if (passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), appUser.getPasswordHash())) {
            appUser.setPasswordHash(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));

            appUserService.save(appUser);
        } else {
            throw new IncorrectPasswordException("Incorrect old password");
        }
    }
}
