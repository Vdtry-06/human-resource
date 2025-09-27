package com.spring.human.resource.server.services;

import com.spring.human.lib.constants.Constants;
import com.spring.human.lib.enumerated.Language;
import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.resource.server.caches.ICacheData;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.configs.security.JwtProvider;
import com.spring.human.resource.server.configs.security.SecurityHelper;
import com.spring.human.resource.server.entities.User;
import com.spring.human.resource.server.payload.auth.LoginRequest;
import com.spring.human.resource.server.payload.auth.LoginResponse;
import com.spring.human.resource.server.payload.auth.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserService userService;
    private final SecurityHelper securityHelper;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MessageSourceHelper messageSourceHelper;
    private final ICacheData<String> caches;

    public LoginResponse login(LoginRequest request) {
        User user;
        if (!isMail(request.getIdentifier())) {
            user = userService.findByFields(Map.of("username", request.getIdentifier()));
        } else {
            user = userService.findByFields(Map.of("email", request.getIdentifier()));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String accessToken = jwtProvider.generateToken(user.getUsername());
            final String refreshToken = UUID.randomUUID().toString();

            return LoginResponse.builder()
                    .id(user.getUserId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .token(TokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("{} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadRequestException(messageSourceHelper.getMessage("error.userNotFound"));
        }
    }

    public void logout(HttpServletRequest request) {
        try {
            String username = securityHelper.getCurrentUserLogin();
            String jwt = jwtProvider.getJwtFromRequest(request);
            if (jwt == null) {
                log.error("No JWT token found in logout request");
                throw new BadRequestException(messageSourceHelper.getMessage("warning.noToken"));
            }
            SecurityContextHolder.clearContext();
            try {
                caches.save(jwt, username, jwtProvider.getExpirationTime());
                log.info("User {} logged out successfully, token blacklisted", username);
            } catch (RedisConnectionFailureException e) {
                log.warn("Redis unavailable, skipping token blacklist for user {}: {}", username, e.getMessage());
                // Proceed with logout even if Redis is down
            }
            log.info("User {} logged out successfully", username);
        } catch (Exception e) {
            log.error("Logout error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadRequestException(messageSourceHelper.getMessage("error.logoutFailed"));
        }
    }

    private boolean isMail(String identifier) {
        return identifier.contains(Constants.MailFormat.PTIT_MAIL)
                || identifier.contains(Constants.MailFormat.SAMSUNG_MAIL)
                || identifier.contains(Constants.MailFormat.PTIT_EDU_MAIL)
                || identifier.contains(Constants.MailFormat.STARDARD_EMAIL)
                || identifier.contains(Constants.MailFormat.FACEBOOK_MAIL);
    }

    @Deprecated
    private Language getLegion() {
        final Locale locale = LocaleContextHolder.getLocale();
        switch (locale.getLanguage()) {
            case "en":
                return Language.EN;
            case "vi":
                return Language.VI;
            case "ja":
                return Language.JA;
            case "zh":
                return Language.ZH;
            case "ko":
                return Language.KO;
            default:
                return Language.DEFAULT;
        }
    }

}
