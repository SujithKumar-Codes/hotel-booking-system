package com.hotel.booking.controller;

import com.hotel.booking.dto.RoomRequestDto;
import com.hotel.booking.dto.RoomSummaryResponseDto;
import com.hotel.booking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/api/hotels/{id}/rooms")
    public ResponseEntity<Page<RoomSummaryResponseDto>> getRoomsByHotel(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(id, pageable));
    }

    @GetMapping("/api/rooms/available")
    public ResponseEntity<List<RoomSummaryResponseDto>> getAvailableRooms(
            @RequestParam String city,
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(roomService.getAvailableRooms(city, date));
    }

    @PostMapping("/api/hotels/{id}/rooms")
    public ResponseEntity<RoomSummaryResponseDto> addRoomToHotel(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoomToHotel(id, requestDto));
    }

    @PutMapping("/api/rooms/{id}")
    public ResponseEntity<RoomSummaryResponseDto> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto requestDto) {
        return ResponseEntity.ok(roomService.updateRoom(id, requestDto));
    }

    @DeleteMapping("/api/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}