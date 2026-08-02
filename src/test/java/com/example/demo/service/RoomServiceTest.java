package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dao.RoomRepository;
import com.example.demo.domain.entity.Room;
import com.example.demo.service.exception.ConflictException;
import com.example.demo.service.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

  @Mock private RoomRepository roomRepository;

  @InjectMocks private RoomService roomService;

  @Test
  void shouldGenerateOneSeatPerCapacityUnit() {
    when(roomRepository.existsByNumber("A1")).thenReturn(false);
    when(roomRepository.save(any(Room.class))).thenAnswer(InvocationOnMock::getArgument);

    Room room = roomService.createRoom("A1", 5);

    assertThat(room.getSeats()).hasSize(5);
    assertThat(room.getSeats())
        .extracting(seat -> seat.getNumber())
        .containsExactlyInAnyOrder("1", "2", "3", "4", "5");
    assertThat(room.getSeats()).allMatch(seat -> seat.getRoom() == room);
  }

  @Test
  void shouldRejectDuplicateRoomNumber() {
    when(roomRepository.existsByNumber("A1")).thenReturn(true);

    assertThatThrownBy(() -> roomService.createRoom("A1", 5))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void shouldThrowWhenRoomNotFound() {
    UUID id = UUID.randomUUID();
    when(roomRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> roomService.getById(id)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldReturnRoomWhenFound() {
    UUID id = UUID.randomUUID();
    Room room = Room.builder().id(id).number("A1").capacity(5).build();
    when(roomRepository.findById(id)).thenReturn(Optional.of(room));

    assertThat(roomService.getById(id)).isEqualTo(room);
  }
}
