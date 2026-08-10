package com.example.demo.dao;

import com.example.demo.domain.entity.Projection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectionRepository extends JpaRepository<Projection, UUID> {

  List<Projection> findByRoomId(UUID roomId);
}
