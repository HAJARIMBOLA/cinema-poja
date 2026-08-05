package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.dao.ProjectionRepository;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.entity.Projection;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.enums.Genre;
import com.example.demo.service.exception.ConflictException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectionServiceTest {

  @Mock private ProjectionRepository projectionRepository;
  @Mock private MovieService movieService;
  @Mock private RoomService roomService;

  @InjectMocks private ProjectionService projectionService;

  private final UUID movieId = UUID.randomUUID();
  private final UUID roomId = UUID.randomUUID();
  private final Room room = Room.builder().id(roomId).number("A1").capacity(50).build();

  private Movie movieWithDuration(Duration duration) {
    return Movie.builder()
        .id(movieId)
        .title("Inception")
        .genre(Genre.SCI_FI)
        .duration(duration)
        .build();
  }

  @Test
  void shouldCreateProjectionWhenRoomIsFree() {
    Movie movie = movieWithDuration(Duration.ofMinutes(120));
    when(movieService.getById(movieId)).thenReturn(movie);
    when(roomService.getById(roomId)).thenReturn(room);
    when(projectionRepository.findByRoomId(roomId)).thenReturn(List.of());
    when(projectionRepository.save(any(Projection.class)))
        .thenAnswer(InvocationOnMock::getArgument);

    Instant datetime = Instant.parse("2026-09-01T18:00:00Z");
    Projection projection =
        projectionService.createProjection(movieId, roomId, datetime, BigDecimal.TEN);

    assertThat(projection.getMovie()).isEqualTo(movie);
    assertThat(projection.getRoom()).isEqualTo(room);
    assertThat(projection.getDatetime()).isEqualTo(datetime);
  }

  @Test
  void shouldRejectProjectionOverlappingAnExistingOne() {
    Movie movie = movieWithDuration(Duration.ofMinutes(120));
    when(movieService.getById(movieId)).thenReturn(movie);
    when(roomService.getById(roomId)).thenReturn(room);

    Projection existing =
        Projection.builder()
            .datetime(Instant.parse("2026-09-01T18:00:00Z"))
            .movie(movieWithDuration(Duration.ofMinutes(120)))
            .room(room)
            .build();
    when(projectionRepository.findByRoomId(roomId)).thenReturn(List.of(existing));

    Instant overlappingStart = Instant.parse("2026-09-01T19:00:00Z");

    assertThatThrownBy(
            () ->
                projectionService.createProjection(
                    movieId, roomId, overlappingStart, BigDecimal.TEN))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void shouldAllowProjectionStartingExactlyWhenPreviousOneEnds() {
    Movie movie = movieWithDuration(Duration.ofMinutes(120));
    when(movieService.getById(movieId)).thenReturn(movie);
    when(roomService.getById(roomId)).thenReturn(room);

    Projection existing =
        Projection.builder()
            .datetime(Instant.parse("2026-09-01T18:00:00Z"))
            .movie(movieWithDuration(Duration.ofMinutes(120)))
            .room(room)
            .build();
    when(projectionRepository.findByRoomId(roomId)).thenReturn(List.of(existing));
    when(projectionRepository.save(any(Projection.class)))
        .thenAnswer(InvocationOnMock::getArgument);

    Instant backToBackStart = Instant.parse("2026-09-01T20:00:00Z");

    Projection projection =
        projectionService.createProjection(movieId, roomId, backToBackStart, BigDecimal.TEN);

    assertThat(projection.getDatetime()).isEqualTo(backToBackStart);
  }

  @Test
  void shouldAllowProjectionInDifferentRoomAtSameTime() {
    Movie movie = movieWithDuration(Duration.ofMinutes(120));
    when(movieService.getById(movieId)).thenReturn(movie);
    when(roomService.getById(roomId)).thenReturn(room);

    when(projectionRepository.findByRoomId(roomId)).thenReturn(List.of());
    when(projectionRepository.save(any(Projection.class)))
        .thenAnswer(InvocationOnMock::getArgument);

    Instant datetime = Instant.parse("2026-09-01T18:00:00Z");
    Projection projection =
        projectionService.createProjection(movieId, roomId, datetime, BigDecimal.TEN);

    assertThat(projection.getRoom().getId()).isEqualTo(roomId);
  }
}
