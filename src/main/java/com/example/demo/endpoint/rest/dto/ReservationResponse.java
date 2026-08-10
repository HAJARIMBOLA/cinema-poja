package com.example.demo.endpoint.rest.dto;

import com.example.demo.domain.entity.Reservation;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReservationResponse(
    UUID id,
    Instant createdAt,
    UUID userId,
    UUID projectionId,
    List<String> seatNumbers,
    BigDecimal totalPrice) {

  public static ReservationResponse from(Reservation reservation, BigDecimal totalPrice) {
    return new ReservationResponse(
        reservation.getId(),
        reservation.getCreatedAt(),
        reservation.getUser().getId(),
        reservation.getProjection().getId(),
        reservation.getSeats().stream().map(seat -> seat.getNumber()).toList(),
        totalPrice);
  }
}
