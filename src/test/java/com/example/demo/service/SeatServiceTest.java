package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.demo.dao.SeatRepository;
import com.example.demo.domain.entity.Room;
import com.example.demo.domain.entity.Seat;
import com.example.demo.service.exception.NotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

  @Mock private SeatRepository seatRepository;

  @InjectMocks private SeatService seatService;

  private final Room room = Room.builder().id(UUID.randomUUID()).number("A1").capacity(2).build();

  @Test
  void shouldReturnAllSeatsWhenAllExist() {
    Seat seat1 = Seat.builder().id(UUID.randomUUID()).number("1").room(room).build();
    Seat seat2 = Seat.builder().id(UUID.randomUUID()).number("2").room(room).build();
    Set<UUID> ids = Set.of(seat1.getId(), seat2.getId());
    when(seatRepository.findAllById(ids)).thenReturn(List.of(seat1, seat2));

    Set<Seat> seats = seatService.getAllByIds(ids);

    assertThat(seats).containsExactlyInAnyOrder(seat1, seat2);
  }

  @Test
  void shouldThrowWhenSomeSeatsAreMissing() {
    Seat seat1 = Seat.builder().id(UUID.randomUUID()).number("1").room(room).build();
    UUID missingId = UUID.randomUUID();
    Set<UUID> ids = Set.of(seat1.getId(), missingId);
    when(seatRepository.findAllById(ids)).thenReturn(List.of(seat1));

    assertThatThrownBy(() -> seatService.getAllByIds(ids)).isInstanceOf(NotFoundException.class);
  }
}
