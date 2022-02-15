package com.example.demo.security;

import com.example.demo.model.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${jwt.app.jwtSecret}")
    private String jwtSecret;

    @Value("${jwt.app.jwtExpirationMs}")
    private String jwtExpirationMs;

    public String generateJwtToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + Integer.parseInt(jwtExpirationMs));
    }

    public Claims getClaimsFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = getClaimsFromJwtToken(token);
        if (claims != null && isTokenExpired(claims)) {
            return claims.getSubject();
        }
        return null;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}

