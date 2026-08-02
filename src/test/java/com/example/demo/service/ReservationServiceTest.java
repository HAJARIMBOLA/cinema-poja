package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.dao.ReservationRepository;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.entity.Projection;
import com.example.demo.domain.entity.Reservation;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.Seat;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Genre;
import com.example.demo.domain.enums.UserRole;
import com.example.demo.service.exception.ConflictException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;
  @Mock private UserService userService;
  @Mock private ProjectionService projectionService;
  @Mock private SeatService seatService;

  @InjectMocks private ReservationService reservationService;

  private final UUID userId = UUID.randomUUID();
  private final UUID projectionId = UUID.randomUUID();
  private final Room room = Room.builder().id(UUID.randomUUID()).number("A1").capacity(10).build();

  private User aClient() {
    return User.builder()
        .id(userId)
        .firstName("Jean")
        .lastName("Dupont")
        .birthdate(LocalDate.of(1990, 1, 1))
        .email("jean.dupont@example.com")
        .password("hashed")
        .phone("+33612345678")
        .role(UserRole.CLIENT)
        .build();
  }

  private Seat seatInRoom() {
    Seat seat = Seat.builder().id(UUID.randomUUID()).number("1").room(room).build();
    room.getSeats().add(seat);
    return seat;
  }

  private Projection aProjection(BigDecimal seatPrice) {
    Movie movie =
        Movie.builder()
            .title("Inception")
            .genre(Genre.SCI_FI)
            .duration(Duration.ofMinutes(148))
            .build();
    return Projection.builder()
        .id(projectionId)
        .datetime(Instant.now())
        .seatPrice(seatPrice)
        .movie(movie)
        .room(room)
        .build();
  }

  @Test
  void shouldCreateReservationWhenSeatsAreAvailable() {
    Seat seat = seatInRoom();
    when(userService.getById(userId)).thenReturn(aClient());
    when(projectionService.getById(projectionId)).thenReturn(aProjection(BigDecimal.TEN));
    when(seatService.getAllByIds(Set.of(seat.getId()))).thenReturn(Set.of(seat));
    when(reservationRepository.findByProjectionId(projectionId)).thenReturn(List.of());
    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(InvocationOnMock::getArgument);

    Reservation reservation =
        reservationService.createReservation(userId, projectionId, Set.of(seat.getId()));

    assertThat(reservation.getSeats()).containsExactly(seat);
    assertThat(reservation.getUser().getId()).isEqualTo(userId);
    assertThat(reservation.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldRejectWhenSeatAlreadyBookedForThisProjection() {
    Seat seat = seatInRoom();
    when(userService.getById(userId)).thenReturn(aClient());
    when(projectionService.getById(projectionId)).thenReturn(aProjection(BigDecimal.TEN));
    when(seatService.getAllByIds(Set.of(seat.getId()))).thenReturn(Set.of(seat));

    Reservation existingReservation = Reservation.builder().seats(Set.of(seat)).build();
    when(reservationRepository.findByProjectionId(projectionId))
        .thenReturn(List.of(existingReservation));

    assertThatThrownBy(
            () -> reservationService.createReservation(userId, projectionId, Set.of(seat.getId())))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void shouldRejectWhenSeatDoesNotBelongToProjectionRoom() {
    Room otherRoom = Room.builder().id(UUID.randomUUID()).number("B2").capacity(10).build();
    Seat foreignSeat = Seat.builder().id(UUID.randomUUID()).number("1").room(otherRoom).build();

    when(userService.getById(userId)).thenReturn(aClient());
    when(projectionService.getById(projectionId)).thenReturn(aProjection(BigDecimal.TEN));
    when(seatService.getAllByIds(Set.of(foreignSeat.getId()))).thenReturn(Set.of(foreignSeat));

    assertThatThrownBy(
            () ->
                reservationService.createReservation(
                    userId, projectionId, Set.of(foreignSeat.getId())))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void shouldRejectReservationWithNoSeats() {
    assertThatThrownBy(() -> reservationService.createReservation(userId, projectionId, Set.of()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldComputeTotalPriceAsSeatPriceTimesSeatCount() {
    Seat seat1 = seatInRoom();
    Seat seat2 = seatInRoom();
    Reservation reservation =
        Reservation.builder()
            .projection(aProjection(BigDecimal.valueOf(12.5)))
            .seats(Set.of(seat1, seat2))
            .build();

    BigDecimal total = reservationService.totalPrice(reservation);

    assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(25.0));
  }
}
