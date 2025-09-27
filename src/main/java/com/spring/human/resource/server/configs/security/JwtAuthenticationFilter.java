package com.spring.human.resource.server.configs.security;

import com.spring.human.resource.server.caches.ICacheData;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final MessageSourceHelper messageSourceHelper;
    private final ICacheData<String> caches;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String[] publicUrls = {
                "/users/login",
                "/v3/api-docs",
                "/v3/api-docs/",
                "/swagger-ui.html",
                "/swagger-ui/",
                "/webjars/",
                "/swagger-ui/index.html"
        };

        // Bypass authentication for public URLs
        for (String publicUrl : publicUrls) {
            if (path.startsWith(publicUrl)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Extract JWT token
        String jwt = jwtProvider.getJwtFromRequest(request);
        if (jwt == null || jwt.isEmpty()) {
            log.error("No JWT token provided for path: {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, messageSourceHelper.getMessage("warning.accessDenied"));
            return;
        }

        try {
            // Validate token and extract username
            if (!jwtProvider.validateToken(jwt)) {
                log.error("Invalid JWT token for path: {}", path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, messageSourceHelper.getMessage("warning.accessDenied"));
                return;
            }

            String username = jwtProvider.getUsernameFromToken(jwt);
            try {
                String existedUsernameToken = caches.find(jwt);
                if (existedUsernameToken != null) {
                    log.error("Token is banned for username: {}", username);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, messageSourceHelper.getMessage("warning.tokenBanned"));
                    return;
                }
            } catch (RedisConnectionFailureException e) {
                log.warn("Redis unavailable, skipping blacklist check for token: {}", jwt);
            }

            // Load user details and set authentication
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, jwt, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT validation failed for path {}: {}", path, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, messageSourceHelper.getMessage("warning.accessDenied"));
        }
    }
}