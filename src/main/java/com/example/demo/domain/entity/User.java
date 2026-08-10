package com.example.demo.domain.entity;

import com.example.demo.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "password")
public class User {

  @Id @GeneratedValue private UUID id;

  @NotBlank
  @Column(nullable = false)
  private String firstName;

  @NotBlank
  @Column(nullable = false)
  private String lastName;

  @Past
  @Column(nullable = false)
  private LocalDate birthdate;

  @NotBlank
  @Email
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank
  @Column(nullable = false)
  private String password;

  @NotBlank
  @Column(nullable = false)
  private String phone;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;
}
