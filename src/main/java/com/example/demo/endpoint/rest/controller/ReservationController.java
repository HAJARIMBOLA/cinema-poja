package com.example.demo.endpoint.rest.controller;

import com.example.demo.domain.entity.Reservation;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.endpoint.rest.dto.ReservationRequest;
import com.example.demo.endpoint.rest.dto.ReservationResponse;
import com.example.demo.security.AuthenticatedUser;
import com.example.demo.service.AuthorizationRules;
import com.example.demo.service.ReservationService;
import com.example.demo.service.exception.ForbiddenException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  /**
   * GET /reservations: 403 for CLIENTS, 200 for MANAGERS and EMPLOYEES. Role check is enforced by
   * SecurityConfig; only MANAGER/EMPLOYEE tokens can reach this method.
   */
  @GetMapping
  public List<ReservationResponse> getAll() {
    return reservationService.getAll().stream()
        .map(
            reservation ->
                ReservationResponse.from(reservation, reservationService.totalPrice(reservation)))
        .toList();
  }

  /**
   * GET /reservationById: 200 if the CLIENT owns the reservation, 403 if it belongs to another
   * CLIENT, 200 for MANAGERS and EMPLOYEES. Ownership depends on the resource, so it is checked
   * here rather than in SecurityConfig.
   */
  @GetMapping("/{id}")
  public ReservationResponse getById(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser requester) {
    Reservation reservation = reservationService.getById(id);

    boolean allowed =
        AuthorizationRules.canViewReservation(
            UserRole.valueOf(requester.role()), requester.id(), reservation.getUser().getId());
    if (!allowed) {
      throw new ForbiddenException("You are not allowed to view this reservation");
    }

    return ReservationResponse.from(reservation, reservationService.totalPrice(reservation));
  }

  /**
   * PUT /reservation: 403 for CLIENTS, 200 for EMPLOYEES and MANAGERS. Role check is enforced by
   * SecurityConfig; only MANAGER/EMPLOYEE tokens can reach this method.
   */
  @PutMapping
  public ResponseEntity<ReservationResponse> create(
      @Valid @RequestBody ReservationRequest request) {
    Reservation reservation =
        reservationService.createReservation(
            request.userId(), request.projectionId(), request.seatIds());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ReservationResponse.from(reservation, reservationService.totalPrice(reservation)));
  }
}
