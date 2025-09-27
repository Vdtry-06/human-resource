package com.spring.human.resource.server.configs.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.access-token.expiration}")
    private long expirationTime;

    @Value("${jwt.refresh-token.expiration}")
    private long expirationRefreshTime;

    @Getter
    @Value("${jwt.secret-key}")
    private String secretKey;


    private final String AUTH_PREFIX = "Bearer ";
    private final String HEADER = "Authorization";

    /*
        Mã hóa token:
           header: {
              "alg": "HS256",
              "typ": "JWT"
           }
           payload: {
              "sub": "${username}",
              "exp": ${expiryDate},
              "iat": ${date.now}
           }
           signature: mã hóa bởi header và payload cùng 1 secretKey {
                HMACSHA256(
                    base64UrlEncode(header) + "." +
                    base64UrlEncode(payload),
                    secretKey,
                )
           }

    */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder().setHeader(Map.of("typ", "JWT"))
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /*
        Giải mã để lấy subject: username
    */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /*
        Xác thực token:
            1. Sai format
            2. Hết hạn
            3. Thuật toán không hỗ trợ
            4. Rỗng hoặc null
    */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    /*
        Lấy JWT từ HTTP request header
        Nếu header "Authorization" tồn tại và bắt đầu bằng "Bearer ", thì trả về JWT
    */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTH_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }
}
