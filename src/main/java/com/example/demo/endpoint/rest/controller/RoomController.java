package com.example.demo.endpoint.rest.controller;

import com.example.demo.domain.entity.Room;
import com.example.demo.endpoint.rest.dto.RoomRequest;
import com.example.demo.endpoint.rest.dto.RoomResponse;
import com.example.demo.service.RoomService;
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
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

  private final RoomService roomService;

  @GetMapping
  public List<RoomResponse> getAll() {
    return roomService.getAll().stream().map(RoomResponse::from).toList();
  }

  @GetMapping("/{id}")
  public RoomResponse getById(@PathVariable UUID id) {
    return RoomResponse.from(roomService.getById(id));
  }

  @PutMapping
  public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
    Room room = roomService.createRoom(request.number(), request.capacity());
    return ResponseEntity.status(HttpStatus.CREATED).body(RoomResponse.from(room));
  }
}
