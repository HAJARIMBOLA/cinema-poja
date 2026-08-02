package com.example.demo.service;

import com.example.demo.domain.enums.UserRole;
import java.util.UUID;

/**
 * Centralizes the access-control rules described in the project specification, so they are
 * defined once, are unit-testable in isolation, and can be reused by both controllers (via
 * security) and services.
 */
public final class AuthorizationRules {

  private AuthorizationRules() {}

  /** PUT /movies: MANAGERS only. */
  public static boolean canManageMovies(UserRole role) {
    return role == UserRole.MANAGER;
  }

  /** PUT /projection: MANAGERS only. */
  public static boolean canManageProjections(UserRole role) {
    return role == UserRole.MANAGER;
  }

  /** GET /projections: everyone, including anonymous/all roles. */
  public static boolean canListProjections(UserRole role) {
    return true;
  }

  /** GET /reservations (list all): MANAGERS and EMPLOYEES only. */
  public static boolean canListAllReservations(UserRole role) {
    return role == UserRole.MANAGER || role == UserRole.EMPLOYEE;
  }

  /** PUT /reservation: MANAGERS and EMPLOYEES only. */
  public static boolean canManageReservations(UserRole role) {
    return role == UserRole.MANAGER || role == UserRole.EMPLOYEE;
  }

  /**
   * GET /reservationById: MANAGERS and EMPLOYEES can view any reservation; a CLIENT can only view
   * their own.
   */
  public static boolean canViewReservation(UserRole requesterRole, UUID requesterId, UUID ownerId) {
    if (requesterRole == UserRole.MANAGER || requesterRole == UserRole.EMPLOYEE) {
      return true;
    }
    return requesterId != null && requesterId.equals(ownerId);
  }
}
