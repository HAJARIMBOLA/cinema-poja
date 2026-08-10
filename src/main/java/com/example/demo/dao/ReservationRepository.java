package com.example.demo.dao;

import com.example.demo.domain.entity.Reservation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

  List<Reservation> findByUserId(UUID userId);

  List<Reservation> findByProjectionId(UUID projectionId);
}
