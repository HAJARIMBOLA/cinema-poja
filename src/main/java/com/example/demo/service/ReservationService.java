package com.example.demo.service;

import com.example.demo.dao.ReservationRepository;
import com.example.demo.domain.entity.Projection;
import com.example.demo.domain.entity.Reservation;
import com.example.demo.domain.entity.Seat;
import com.example.demo.domain.entity.User;
import com.example.demo.service.exception.ConflictException;
import com.example.demo.service.exception.NotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final UserService userService;
  private final ProjectionService projectionService;
  private final SeatService seatService;

  @Transactional
  public Reservation createReservation(UUID userId, UUID projectionId, Set<UUID> seatIds) {
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("A reservation must contain at least one seat");
    }

    User user = userService.getById(userId);
    Projection projection = projectionService.getById(projectionId);
    Set<Seat> seats = seatService.getAllByIds(seatIds);

    Set<UUID> roomSeatIds =
        projection.getRoom().getSeats().stream().map(Seat::getId).collect(Collectors.toSet());
    boolean allSeatsBelongToRoom =
        seats.stream().allMatch(seat -> roomSeatIds.contains(seat.getId()));
    if (!allSeatsBelongToRoom) {
      throw new ConflictException("One or more seats do not belong to the projection's room");
    }

    Set<UUID> alreadyBookedSeatIds =
        reservationRepository.findByProjectionId(projectionId).stream()
            .flatMap(reservation -> reservation.getSeats().stream())
            .map(Seat::getId)
            .collect(Collectors.toSet());
    boolean anySeatAlreadyBooked =
        seats.stream().anyMatch(seat -> alreadyBookedSeatIds.contains(seat.getId()));
    if (anySeatAlreadyBooked) {
      throw new ConflictException("One or more seats are already booked for this projection");
    }

    Reservation reservation =
        Reservation.builder()
            .createdAt(Instant.now())
            .user(user)
            .projection(projection)
            .seats(seats)
            .build();

    return reservationRepository.save(reservation);
  }

  public BigDecimal totalPrice(Reservation reservation) {
    return reservation
        .getProjection()
        .getSeatPrice()
        .multiply(BigDecimal.valueOf(reservation.getSeats().size()));
  }

  public Reservation getById(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Reservation " + id + " not found"));
  }

  public List<Reservation> getAll() {
    return reservationRepository.findAll();
  }

  public List<Reservation> getByUserId(UUID userId) {
    return reservationRepository.findByUserId(userId);
  }
}
