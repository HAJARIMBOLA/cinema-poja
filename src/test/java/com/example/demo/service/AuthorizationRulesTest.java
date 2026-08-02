package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.enums.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthorizationRulesTest {

  @Test
  void onlyManagersCanManageMovies() {
    assertThat(AuthorizationRules.canManageMovies(UserRole.MANAGER)).isTrue();
    assertThat(AuthorizationRules.canManageMovies(UserRole.EMPLOYEE)).isFalse();
    assertThat(AuthorizationRules.canManageMovies(UserRole.CLIENT)).isFalse();
  }

  @Test
  void onlyManagersCanManageProjections() {
    assertThat(AuthorizationRules.canManageProjections(UserRole.MANAGER)).isTrue();
    assertThat(AuthorizationRules.canManageProjections(UserRole.EMPLOYEE)).isFalse();
    assertThat(AuthorizationRules.canManageProjections(UserRole.CLIENT)).isFalse();
  }

  @Test
  void everyoneCanListProjections() {
    for (UserRole role : UserRole.values()) {
      assertThat(AuthorizationRules.canListProjections(role)).isTrue();
    }
  }

  @Test
  void onlyManagersAndEmployeesCanListAllReservations() {
    assertThat(AuthorizationRules.canListAllReservations(UserRole.MANAGER)).isTrue();
    assertThat(AuthorizationRules.canListAllReservations(UserRole.EMPLOYEE)).isTrue();
    assertThat(AuthorizationRules.canListAllReservations(UserRole.CLIENT)).isFalse();
  }

  @Test
  void onlyManagersAndEmployeesCanManageReservations() {
    assertThat(AuthorizationRules.canManageReservations(UserRole.MANAGER)).isTrue();
    assertThat(AuthorizationRules.canManageReservations(UserRole.EMPLOYEE)).isTrue();
    assertThat(AuthorizationRules.canManageReservations(UserRole.CLIENT)).isFalse();
  }

  @Test
  void managersAndEmployeesCanViewAnyReservation() {
    UUID someoneElse = UUID.randomUUID();
    assertThat(
            AuthorizationRules.canViewReservation(UserRole.MANAGER, UUID.randomUUID(), someoneElse))
        .isTrue();
    assertThat(
            AuthorizationRules.canViewReservation(
                UserRole.EMPLOYEE, UUID.randomUUID(), someoneElse))
        .isTrue();
  }

  @Test
  void clientCanViewOwnReservation() {
    UUID clientId = UUID.randomUUID();

    assertThat(AuthorizationRules.canViewReservation(UserRole.CLIENT, clientId, clientId)).isTrue();
  }

  @Test
  void clientCannotViewAnotherClientReservation() {
    UUID clientId = UUID.randomUUID();
    UUID otherClientId = UUID.randomUUID();

    assertThat(AuthorizationRules.canViewReservation(UserRole.CLIENT, clientId, otherClientId))
        .isFalse();
  }
}
