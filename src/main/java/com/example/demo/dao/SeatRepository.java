package com.example.demo.dao;

import com.example.demo.domain.entity.Seat;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

  List<Seat> findByRoomId(UUID roomId);
}
