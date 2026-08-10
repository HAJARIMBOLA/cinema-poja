package com.example.demo.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.enums.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserTest {

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

  private User.UserBuilder validUserBuilder() {
    return User.builder()
        .firstName("Jean")
        .lastName("Dupont")
        .birthdate(LocalDate.of(1990, 1, 1))
        .email("jean.dupont@example.com")
        .password("hashed-password")
        .phone("+33612345678")
        .role(UserRole.CLIENT);
  }

  @Test
  void shouldAcceptValidUser() {
    Set<ConstraintViolation<User>> violations = validator.validate(validUserBuilder().build());

    assertThat(violations).isEmpty();
  }

  @Test
  void shouldRejectInvalidEmail() {
    User user = validUserBuilder().email("not-an-email").build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  void shouldRejectFutureBirthdate() {
    User user = validUserBuilder().birthdate(LocalDate.now().plusDays(1)).build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("birthdate"));
  }

  @Test
  void shouldRejectBlankFirstName() {
    User user = validUserBuilder().firstName(" ").build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
  }

  @Test
  void shouldRejectMissingRole() {
    User user = validUserBuilder().role(null).build();

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("role"));
  }

  @Test
  void equalityShouldBeBasedOnId() {
    UUID id = UUID.randomUUID();
    User user1 = validUserBuilder().id(id).build();
    User user2 = validUserBuilder().id(id).email("other@example.com").build();

    assertThat(user1).isEqualTo(user2);
  }

  @Test
  void toStringShouldNotExposePassword() {
    User user = validUserBuilder().password("super-secret").build();

    assertThat(user.toString()).doesNotContain("super-secret");
  }
}
