package com.example.demo.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.entity.Projection;
import com.example.demo.domain.entity.Reservation;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.Seat;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Genre;
import com.example.demo.domain.enums.UserRole;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RepositoryIT extends FacadeIT {

  @Autowired private UserRepository userRepository;
  @Autowired private RoomRepository roomRepository;
  @Autowired private SeatRepository seatRepository;
  @Autowired private MovieRepository movieRepository;
  @Autowired private ProjectionRepository projectionRepository;
  @Autowired private ReservationRepository reservationRepository;

  @Test
  void shouldPersistAndRetrieveUserByEmail() {
    User user =
        userRepository.save(
            User.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .birthdate(LocalDate.of(1990, 1, 1))
                .email("jean.dupont@example.com")
                .password("hashed")
                .phone("+33612345678")
                .role(UserRole.CLIENT)
                .build());

    assertThat(userRepository.findByEmail("jean.dupont@example.com")).contains(user);
    assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
  }

  @Test
  void shouldCascadeSeatsWhenPersistingRoom() {
    Room room = Room.builder().number("A1").capacity(2).build();
    room.getSeats().add(Seat.builder().number("1").room(room).build());
    room.getSeats().add(Seat.builder().number("2").room(room).build());

    Room saved = roomRepository.save(room);

    assertThat(seatRepository.findByRoomId(saved.getId())).hasSize(2);
  }

  @Test
  void shouldLinkProjectionToMovieAndRoom() {
    Movie movie =
        movieRepository.save(
            Movie.builder()
                .title("Inception")
                .genre(Genre.SCI_FI)
                .description("A mind-bending thriller")
                .duration(Duration.ofMinutes(148))
                .build());
    Room room = roomRepository.save(Room.builder().number("B2").capacity(30).build());

    Projection projection =
        projectionRepository.save(
            Projection.builder()
                .datetime(Instant.now())
                .seatPrice(BigDecimal.valueOf(10))
                .movie(movie)
                .room(room)
                .build());

    assertThat(projectionRepository.findByRoomId(room.getId())).containsExactly(projection);
  }

  @Test
  void shouldPersistReservationWithMultipleSeats() {
    Room room = roomRepository.save(Room.builder().number("C3").capacity(10).build());
    Seat seat1 = seatRepository.save(Seat.builder().number("1").room(room).build());
    Seat seat2 = seatRepository.save(Seat.builder().number("2").room(room).build());
    Movie movie =
        movieRepository.save(
            Movie.builder()
                .title("Amélie")
                .genre(Genre.ROMANCE)
                .duration(Duration.ofMinutes(122))
                .build());
    Projection projection =
        projectionRepository.save(
            Projection.builder()
                .datetime(Instant.now())
                .seatPrice(BigDecimal.valueOf(9.5))
                .movie(movie)
                .room(room)
                .build());
    User client =
        userRepository.save(
            User.builder()
                .firstName("Marie")
                .lastName("Curie")
                .birthdate(LocalDate.of(1985, 5, 5))
                .email("marie.curie@example.com")
                .password("hashed")
                .phone("+33600000000")
                .role(UserRole.CLIENT)
                .build());

    Reservation reservation =
        reservationRepository.save(
            Reservation.builder()
                .createdAt(Instant.now())
                .user(client)
                .projection(projection)
                .seats(Set.of(seat1, seat2))
                .build());

    assertThat(reservationRepository.findByUserId(client.getId())).containsExactly(reservation);
    assertThat(reservationRepository.findByProjectionId(projection.getId()))
        .containsExactly(reservation);
    assertThat(reservationRepository.findById(reservation.getId()).orElseThrow().getSeats())
        .containsExactlyInAnyOrder(seat1, seat2);
  }
}
