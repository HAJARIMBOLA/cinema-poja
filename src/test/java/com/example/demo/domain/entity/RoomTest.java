package com.example.demo.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RoomTest {

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

  @Test
  void shouldAcceptValidRoom() {
    Room room = Room.builder().number("A1").capacity(50).build();

    Set<ConstraintViolation<Room>> violations = validator.validate(room);

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldRejectNonPositiveCapacity() {
    Room room = Room.builder().number("A1").capacity(0).build();

    Set<ConstraintViolation<Room>> violations = validator.validate(room);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capacity"));
  }

  @Test
  void newRoomShouldStartWithNoSeats() {
    Room room = Room.builder().number("A1").capacity(50).build();

    assertThat(room.getSeats()).isEmpty();
  }

  @Test
  void addingASeatShouldLinkItBackToTheRoom() {
    Room room = Room.builder().number("A1").capacity(50).build();
    Seat seat = Seat.builder().number("1").room(room).build();
    room.getSeats().add(seat);

    assertThat(room.getSeats()).containsExactly(seat);
    assertThat(seat.getRoom()).isEqualTo(room);
  }
}
