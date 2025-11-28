package com.project.student.education.security;

import com.project.student.education.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AuthUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshExp;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Add role claim for frontend routing
    public String generateAccessToken(User user) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getUsername()) // sub
                .claim("role", user.getRole().name()) // ✅ role claim
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessExp))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name()) // optional but recommended
                .claim("type", "refresh")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExp))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(
                    Jwts.parserBuilder()
                            .setSigningKey(getKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .get("type", String.class)
            );
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
