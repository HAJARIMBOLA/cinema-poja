package com.example.demo.dao;

import com.example.demo.domain.entity.Room;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, UUID> {

  boolean existsByNumber(String number);
}
