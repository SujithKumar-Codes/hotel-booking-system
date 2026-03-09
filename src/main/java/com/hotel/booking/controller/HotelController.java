package com.hotel.booking.controller;

import com.hotel.booking.dto.HotelDetailResponseDto;
import com.hotel.booking.dto.HotelRequestDto;
import com.hotel.booking.dto.HotelSummaryResponseDto;
import com.hotel.booking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelSummaryResponseDto>> getAllHotels(
            @PageableDefault(size = 10, sort = "starRating") Pageable pageable) {
        return ResponseEntity.ok(hotelService.getAllHotels(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDetailResponseDto> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PostMapping
    public ResponseEntity<HotelDetailResponseDto> createHotel(@RequestBody HotelRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDetailResponseDto> updateHotel(@PathVariable Long id,
                                                              @RequestBody HotelRequestDto requestDto) {
        return ResponseEntity.ok(hotelService.updateHotel(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}