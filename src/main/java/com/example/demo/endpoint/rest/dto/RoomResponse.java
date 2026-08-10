package com.example.demo.endpoint.rest.dto;

import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.Seat;
import java.util.List;
import java.util.UUID;

public record RoomResponse(UUID id, String number, int capacity, List<SeatResponse> seats) {

  public record SeatResponse(UUID id, String number) {
    public static SeatResponse from(Seat seat) {
      return new SeatResponse(seat.getId(), seat.getNumber());
    }
  }

  public static RoomResponse from(Room room) {
    return new RoomResponse(
        room.getId(),
        room.getNumber(),
        room.getCapacity(),
        room.getSeats().stream().map(SeatResponse::from).toList());
  }
}
