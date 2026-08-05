package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expirationMinutes;

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-minutes}") long expirationMinutes) {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(UUID userId, String email, String role) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(email)
        .claim("userId", userId.toString())
        .claim("role", role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
        .signWith(signingKey)
        .compact();
  }

  public Optional<Claims> parseClaims(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
      return Optional.of(claims);
    } catch (JwtException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
