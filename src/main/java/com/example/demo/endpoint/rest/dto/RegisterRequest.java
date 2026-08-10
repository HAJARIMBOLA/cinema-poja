package com.example.demo.endpoint.rest.dto;

import com.example.demo.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record RegisterRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Past @NotNull LocalDate birthdate,
    @Email @NotBlank String email,
    @NotBlank String password,
    @NotBlank String phone,
    @NotNull UserRole role) {}
