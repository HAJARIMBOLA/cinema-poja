package com.example.demo.endpoint.rest.dto;

import com.example.demo.domain.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;

public record MovieRequest(
    @NotBlank String title,
    @NotNull Genre genre,
    String description,
    @NotNull @Positive Long durationMinutes) {

  public Duration duration() {
    return Duration.ofMinutes(durationMinutes);
  }
}
