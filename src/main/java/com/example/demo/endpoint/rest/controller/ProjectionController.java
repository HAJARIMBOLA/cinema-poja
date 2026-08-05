package com.example.demo.endpoint.rest.controller;

import com.example.demo.domain.entity.Projection;
import com.example.demo.endpoint.rest.dto.ProjectionRequest;
import com.example.demo.endpoint.rest.dto.ProjectionResponse;
import com.example.demo.service.ProjectionService;
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
@RequestMapping("/projections")
@RequiredArgsConstructor
public class ProjectionController {

  private final ProjectionService projectionService;

  /** GET /projections: should return 200 for everyone (enforced as permitAll in SecurityConfig). */
  @GetMapping
  public List<ProjectionResponse> getAll() {
    return projectionService.getAll().stream().map(ProjectionResponse::from).toList();
  }

  @GetMapping("/{id}")
  public ProjectionResponse getById(@PathVariable UUID id) {
    return ProjectionResponse.from(projectionService.getById(id));
  }

  /** Authorization (403 for CLIENT/EMPLOYEES, 200 for MANAGERS) is enforced by SecurityConfig. */
  @PutMapping
  public ResponseEntity<ProjectionResponse> create(@Valid @RequestBody ProjectionRequest request) {
    Projection projection =
        projectionService.createProjection(
            request.movieId(), request.roomId(), request.datetime(), request.seatPrice());

    return ResponseEntity.status(HttpStatus.CREATED).body(ProjectionResponse.from(projection));
  }
}
