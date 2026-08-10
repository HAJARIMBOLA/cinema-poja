package com.example.demo.endpoint.rest.controller;

import com.example.demo.domain.entity.Movie;
import com.example.demo.endpoint.rest.dto.MovieRequest;
import com.example.demo.endpoint.rest.dto.MovieResponse;
import com.example.demo.service.MovieService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;

  @GetMapping
  public List<MovieResponse> getAll() {
    return movieService.getAll().stream().map(MovieResponse::from).toList();
  }

  @GetMapping("/{id}")
  public MovieResponse getById(@PathVariable UUID id) {
    return MovieResponse.from(movieService.getById(id));
  }

  @PutMapping
  public ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
    Movie movie =
        movieService.create(
            Movie.builder()
                .title(request.title())
                .genre(request.genre())
                .description(request.description())
                .duration(request.duration())
                .build());

    return ResponseEntity.status(HttpStatus.CREATED).body(MovieResponse.from(movie));
  }
}
