package com.example.demo.endpoint.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RoomRequest(@NotBlank String number, @Positive int capacity) {}
