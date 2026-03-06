package com.hotel.booking.service;

import com.hotel.booking.dto.BookingSummaryResponseDto;
import com.hotel.booking.dto.GuestRequestDto;
import com.hotel.booking.dto.GuestResponseDto;
import com.hotel.booking.entity.Guest;
import com.hotel.booking.exception.GuestNotFoundException;
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

    public GuestResponseDto createGuest(GuestRequestDto requestDto){
        Guest guest = new Guest();
        guest.setName(requestDto.getName());
        guest.setEmail(requestDto.getEmail());
        guest.setPhone(requestDto.getPhone());

        Guest savedGuest = guestRepository.save(guest);

        GuestResponseDto dto = new GuestResponseDto();
        dto.setId(savedGuest.getId());
        dto.setName(savedGuest.getName());
        dto.setEmail(savedGuest.getEmail());
        dto.setPhone(savedGuest.getPhone());

        return dto;
    }

    public GuestResponseDto getGuest(Long id){
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException("No guest found with the id " + id));

        GuestResponseDto dto = new GuestResponseDto();
        dto.setId(guest.getId());
        dto.setName(guest.getName());
        dto.setEmail(guest.getEmail());
        dto.setPhone(guest.getPhone());

        return dto;
    }

    public Page<BookingSummaryResponseDto> getBookingsByGuest(Long guestId, Pageable pageable) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found with id: " + guestId));

        return bookingRepository.findByGuest(guest, pageable)
                .map(booking -> {
                    BookingSummaryResponseDto dto = new BookingSummaryResponseDto();
                    dto.setId(booking.getId());
                    dto.setCheckInDate(booking.getCheckInDate());
                    dto.setCheckOutDate(booking.getCheckOutDate());
                    dto.setStatus(booking.getStatus());
                    return dto;
                });
    }

}
