package com.example.demo.endpoint.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectionRequest(
    @NotNull UUID movieId,
    @NotNull UUID roomId,
    @NotNull Instant datetime,
    @NotNull @Positive BigDecimal seatPrice) {}
