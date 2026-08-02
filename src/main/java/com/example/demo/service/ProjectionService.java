package com.example.demo.service;

import com.example.demo.dao.ProjectionRepository;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.entity.Projection;
import com.example.demo.domain.entity.Room;
import com.example.demo.service.exception.ConflictException;
import com.example.demo.service.exception.NotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectionService {

  private final ProjectionRepository projectionRepository;
  private final MovieService movieService;
  private final RoomService roomService;

  /**
   * Creates a projection after checking that the room is not already booked, at the same time, for
   * the duration of the movie (start inclusive, end exclusive on both sides).
   */
  @Transactional
  public Projection createProjection(
      UUID movieId, UUID roomId, Instant datetime, BigDecimal seatPrice) {
    Movie movie = movieService.getById(movieId);
    Room room = roomService.getById(roomId);

    Instant newStart = datetime;
    Instant newEnd = datetime.plus(movie.getDuration());

    boolean overlaps =
        projectionRepository.findByRoomId(roomId).stream()
            .anyMatch(existing -> overlaps(newStart, newEnd, existing));

    if (overlaps) {
      throw new ConflictException(
          "Room " + room.getNumber() + " is already booked for that time slot");
    }

    Projection projection =
        Projection.builder()
            .datetime(datetime)
            .seatPrice(seatPrice)
            .movie(movie)
            .room(room)
            .build();

    return projectionRepository.save(projection);
  }

  private boolean overlaps(Instant newStart, Instant newEnd, Projection existing) {
    Instant existingStart = existing.getDatetime();
    Instant existingEnd = existingStart.plus(existing.getMovie().getDuration());
    return newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
  }

  public Projection getById(UUID id) {
    return projectionRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Projection " + id + " not found"));
  }

  public List<Projection> getAll() {
    return projectionRepository.findAll();
  }
}
