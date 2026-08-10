package com.example.demo.endpoint.rest.dto;

import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.enums.Genre;
import java.util.UUID;

public record MovieResponse(
    UUID id, String title, Genre genre, String description, long durationMinutes) {

  public static MovieResponse from(Movie movie) {
    return new MovieResponse(
        movie.getId(),
        movie.getTitle(),
        movie.getGenre(),
        movie.getDescription(),
        movie.getDuration().toMinutes());
  }
}
