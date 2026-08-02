package com.example.demo.service;

import com.example.demo.dao.MovieRepository;
import com.example.demo.domain.entity.Movie;
import com.example.demo.service.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;

  public Movie create(Movie movie) {
    return movieRepository.save(movie);
  }

  public Movie getById(UUID id) {
    return movieRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Movie " + id + " not found"));
  }

  public List<Movie> getAll() {
    return movieRepository.findAll();
  }
}
