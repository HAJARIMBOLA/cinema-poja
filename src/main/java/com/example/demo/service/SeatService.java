package com.example.demo.service;

import com.example.demo.dao.SeatRepository;
import com.example.demo.domain.entity.Seat;
import com.example.demo.service.exception.NotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;

  public Set<Seat> getAllByIds(Set<UUID> ids) {
    List<Seat> found = seatRepository.findAllById(ids);
    if (found.size() != ids.size()) {
      Set<UUID> foundIds = found.stream().map(Seat::getId).collect(Collectors.toSet());
      Set<UUID> missing =
          ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
      throw new NotFoundException("Seat(s) not found: " + missing);
    }
    return Set.copyOf(found);
  }

  public List<Seat> getByRoomId(UUID roomId) {
    return seatRepository.findByRoomId(roomId);
  }
}
