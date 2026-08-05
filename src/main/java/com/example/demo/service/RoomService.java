package com.example.demo.service;

import com.example.demo.dao.RoomRepository;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.Seat;
import com.example.demo.service.exception.ConflictException;
import com.example.demo.service.exception.NotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  @Transactional
  public Room createRoom(String number, int capacity) {
    if (roomRepository.existsByNumber(number)) {
      throw new ConflictException("A room with number '" + number + "' already exists");
    }

    Room room = Room.builder().number(number).capacity(capacity).build();
    for (int i = 1; i <= capacity; i++) {
      room.getSeats().add(Seat.builder().number(String.valueOf(i)).room(room).build());
    }

    return roomRepository.save(room);
  }

  public Room getById(UUID id) {
    return roomRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Room " + id + " not found"));
  }

  public java.util.List<Room> getAll() {
    return roomRepository.findAll();
  }
}
