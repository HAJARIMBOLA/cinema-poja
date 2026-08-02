package com.example.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "projections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"movie", "room"})
public class Projection {

  @Id @GeneratedValue private UUID id;

  @NotNull
  @Column(nullable = false)
  private Instant datetime;

  @NotNull
  @Positive
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal seatPrice;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "movie_id", nullable = false)
  private Movie movie;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;
}
