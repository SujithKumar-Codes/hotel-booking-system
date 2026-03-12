package com.hotel.booking.service;

import com.hotel.booking.dto.BookingSummaryResponseDto;
import com.hotel.booking.dto.GuestRequestDto;
import com.hotel.booking.dto.GuestResponseDto;
import com.hotel.booking.entity.Guest;
import com.hotel.booking.exception.GuestNotFoundException;
import com.hotel.booking.mapper.BookingMapper;
import com.hotel.booking.mapper.GuestMapper;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuestMapper guestMapper;

    @Autowired
    private BookingMapper bookingMapper;


    public GuestResponseDto createGuest(GuestRequestDto requestDto){
        Guest guest = guestMapper.toEntity(requestDto);
        Guest savedGuest = guestRepository.save(guest);
        return guestMapper.toResponseDto(savedGuest);
    }

    public GuestResponseDto getGuest(Long id){
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException("No guest found with the id " + id));
        return guestMapper.toResponseDto(guest);
    }

    public Page<BookingSummaryResponseDto> getBookingsByGuest(Long guestId, Pageable pageable) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found with id: " + guestId));

        return bookingRepository.findByGuest(guest, pageable)
                .map(booking -> bookingMapper.toSummaryDto(booking));
    }

}