package com.example.demo.service;

import com.example.demo.domain.enums.UserRole;
import java.util.UUID;

public final class AuthorizationRules {

  private AuthorizationRules() {}

  public static boolean canManageMovies(UserRole role) {
    return role == UserRole.MANAGER;
  }

  public static boolean canManageProjections(UserRole role) {
    return role == UserRole.MANAGER;
  }

  public static boolean canListProjections(UserRole role) {
    return true;
  }

  public static boolean canListAllReservations(UserRole role) {
    return role == UserRole.MANAGER || role == UserRole.EMPLOYEE;
  }

  public static boolean canManageReservations(UserRole role) {
    return role == UserRole.MANAGER || role == UserRole.EMPLOYEE;
  }

  public static boolean canViewReservation(UserRole requesterRole, UUID requesterId, UUID ownerId) {
    if (requesterRole == UserRole.MANAGER || requesterRole == UserRole.EMPLOYEE) {
      return true;
    }
    return requesterId != null && requesterId.equals(ownerId);
  }
}
