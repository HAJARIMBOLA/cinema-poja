package com.example.demo.domain.entity;

import com.example.demo.domain.enums.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "movies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Movie {

  @Id @GeneratedValue private UUID id;

  @NotBlank
  @Column(nullable = false)
  private String title;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  @Column(length = 2000)
  private String description;

  @NotNull
  @Column(nullable = false)
  private Duration duration;
}
