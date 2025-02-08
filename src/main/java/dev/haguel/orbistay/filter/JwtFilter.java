package dev.haguel.orbistay.filter;

import dev.haguel.orbistay.service.JwtService;
import dev.haguel.orbistay.service.UserDetailsCustomService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserDetailsCustomService userDetailsCustomService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("JwtFilter started");
        String path = request.getRequestURI();
        if (path.startsWith("/oauth2/") || path.startsWith("/login/")) {
            log.info("Request path is /oauth2/ or /login/");
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            log.warn("Authorization header is empty or doesn't start with Bearer prefix");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Authorization header is valid");
        String jwt = authHeader.substring(BEARER_PREFIX.length());

        if (jwt != null && jwtService.validateAccessToken(jwt)) {
            log.info("Jwt token is valid");
            final Claims claims = jwtService.getAccessClaims(jwt);
            UserDetails userDetails = userDetailsCustomService.loadUserByUsername(claims.getSubject());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            log.warn("Jwt token is invalid");
        }

        filterChain.doFilter(request, response);
    }
}
