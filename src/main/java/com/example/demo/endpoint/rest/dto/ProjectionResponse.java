package com.example.demo.endpoint.rest.dto;

import com.example.demo.domain.entity.Projection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectionResponse(
    UUID id,
    Instant datetime,
    BigDecimal seatPrice,
    UUID movieId,
    String movieTitle,
    UUID roomId,
    String roomNumber) {

  public static ProjectionResponse from(Projection projection) {
    return new ProjectionResponse(
        projection.getId(),
        projection.getDatetime(),
        projection.getSeatPrice(),
        projection.getMovie().getId(),
        projection.getMovie().getTitle(),
        projection.getRoom().getId(),
        projection.getRoom().getNumber());
  }
}
