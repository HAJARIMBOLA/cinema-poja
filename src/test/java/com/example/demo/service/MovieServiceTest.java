package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.dao.MovieRepository;
import com.example.demo.domain.entity.Movie;
import com.example.demo.domain.enums.Genre;
import com.example.demo.service.exception.NotFoundException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

  @Mock private MovieRepository movieRepository;

  @InjectMocks private MovieService movieService;

  private Movie aMovie() {
    return Movie.builder()
        .title("Inception")
        .genre(Genre.SCI_FI)
        .duration(Duration.ofMinutes(148))
        .build();
  }

  @Test
  void shouldSaveMovie() {
    Movie movie = aMovie();
    when(movieRepository.save(movie)).thenReturn(movie);

    assertThat(movieService.create(movie)).isEqualTo(movie);
  }

  @Test
  void shouldThrowWhenMovieNotFound() {
    UUID id = UUID.randomUUID();
    when(movieRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> movieService.getById(id)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldReturnAllMovies() {
    Movie movie = aMovie();
    when(movieRepository.findAll()).thenReturn(List.of(movie));

    assertThat(movieService.getAll()).containsExactly(movie);
  }
}
