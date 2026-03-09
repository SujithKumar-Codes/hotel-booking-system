package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingSummaryResponseDto;
import com.hotel.booking.dto.GuestRequestDto;
import com.hotel.booking.dto.GuestResponseDto;
import com.hotel.booking.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestResponseDto> createGuest(@RequestBody GuestRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.createGuest(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponseDto> getGuest(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.getGuest(id));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<Page<BookingSummaryResponseDto>> getBookingsByGuest(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(guestService.getBookingsByGuest(id, pageable));
    }
}