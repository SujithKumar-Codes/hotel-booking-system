package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingDetailResponseDto;
import com.hotel.booking.dto.BookingRequestDto;
import com.hotel.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDetailResponseDto> createBooking(@RequestBody BookingRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailResponseDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDetailResponseDto> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}