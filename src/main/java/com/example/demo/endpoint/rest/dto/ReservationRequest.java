package com.example.demo.endpoint.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record ReservationRequest(
    @NotNull UUID userId, @NotNull UUID projectionId, @NotEmpty Set<UUID> seatIds) {}
