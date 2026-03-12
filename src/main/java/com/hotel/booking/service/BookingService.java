package com.hotel.booking.service;

import com.hotel.booking.dto.BookingDetailResponseDto;
import com.hotel.booking.dto.BookingRequestDto;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Guest;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.*;
import com.hotel.booking.mapper.BookingMapper;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.GuestRepository;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private BookingMapper bookingMapper;

    public BookingDetailResponseDto createBooking(BookingRequestDto requestDto) {
        if (!requestDto.getCheckOutDate().isAfter(requestDto.getCheckInDate())) {
            throw new InvalidDateRangeException("Check-out date must be after check-in date");
        }

        Guest guest = guestRepository.findById(requestDto.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found with id: " + requestDto.getGuestId()));

        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + requestDto.getRoomId()));

        if (!room.getIsAvailable()) {
            throw new RoomNotAvailableException("Room is not available for the requested dates");
        }

        Booking booking = new Booking();
        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmationNumber(UUID.randomUUID().toString());
        booking.setGuest(guest);
        booking.setRoom(room);

        room.setIsAvailable(false);
        roomRepository.save(room);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toDetailDto(savedBooking);
    }

    public BookingDetailResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        return bookingMapper.toDetailDto(booking);
    }

    public BookingDetailResponseDto cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidDateRangeException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Room room = booking.getRoom();
        room.setIsAvailable(true);
        roomRepository.save(room);

        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toDetailDto(updatedBooking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        Room room = booking.getRoom();
        room.setIsAvailable(true);
        roomRepository.save(room);

        bookingRepository.delete(booking);
    }
}