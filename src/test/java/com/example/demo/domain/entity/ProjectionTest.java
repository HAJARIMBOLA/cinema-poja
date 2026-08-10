package com.example.demo.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.enums.Genre;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectionTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    factory.close();
  }

  private Movie aMovie() {
    return Movie.builder()
        .title("Inception")
        .genre(Genre.SCI_FI)
        .description("A mind-bending thriller")
        .duration(Duration.ofMinutes(148))
        .build();
  }

  private Room aRoom() {
    return Room.builder().number("A1").capacity(50).build();
  }

  @Test
  void shouldAcceptValidProjection() {
    Projection projection =
        Projection.builder()
            .datetime(Instant.now())
            .seatPrice(BigDecimal.valueOf(12.50))
            .movie(aMovie())
            .room(aRoom())
            .build();

    Set<ConstraintViolation<Projection>> violations = validator.validate(projection);

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldRejectNonPositiveSeatPrice() {
    Projection projection =
        Projection.builder()
            .datetime(Instant.now())
            .seatPrice(BigDecimal.ZERO)
            .movie(aMovie())
            .room(aRoom())
            .build();

    Set<ConstraintViolation<Projection>> violations = validator.validate(projection);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("seatPrice"));
  }

  @Test
  void shouldRejectMissingMovie() {
    Projection projection =
        Projection.builder()
            .datetime(Instant.now())
            .seatPrice(BigDecimal.TEN)
            .room(aRoom())
            .build();

    Set<ConstraintViolation<Projection>> violations = validator.validate(projection);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("movie"));
  }
}
