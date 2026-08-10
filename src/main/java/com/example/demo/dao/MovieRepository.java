package com.example.demo.dao;

import com.example.demo.domain.entity.Movie;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, UUID> {}
