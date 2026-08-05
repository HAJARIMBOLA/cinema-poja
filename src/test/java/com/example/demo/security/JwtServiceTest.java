package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static final String SECRET = "unit-test-secret-key-must-be-long-enough-for-hs256!!";

  private final JwtService jwtService = new JwtService(SECRET, 60);

  @Test
  void shouldGenerateTokenContainingUserClaims() {
    UUID userId = UUID.randomUUID();

    String token = jwtService.generateToken(userId, "jean.dupont@example.com", "CLIENT");
    Optional<Claims> claims = jwtService.parseClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getSubject()).isEqualTo("jean.dupont@example.com");
    assertThat(claims.get().get("userId", String.class)).isEqualTo(userId.toString());
    assertThat(claims.get().get("role", String.class)).isEqualTo("CLIENT");
  }

  @Test
  void shouldRejectMalformedToken() {
    Optional<Claims> claims = jwtService.parseClaims("not-a-valid-jwt");

    assertThat(claims).isEmpty();
  }

  @Test
  void shouldRejectTokenSignedWithADifferentSecret() {
    JwtService otherService =
        new JwtService("a-completely-different-secret-key-also-long-enough!!", 60);
    String token = otherService.generateToken(UUID.randomUUID(), "a@example.com", "MANAGER");

    Optional<Claims> claims = jwtService.parseClaims(token);

    assertThat(claims).isEmpty();
  }

  @Test
  void shouldRejectExpiredToken() throws InterruptedException {
    JwtService shortLivedService = new JwtService(SECRET, 0);
    String token = shortLivedService.generateToken(UUID.randomUUID(), "a@example.com", "CLIENT");

    Thread.sleep(50);

    Optional<Claims> claims = shortLivedService.parseClaims(token);

    assertThat(claims).isEmpty();
  }
}
