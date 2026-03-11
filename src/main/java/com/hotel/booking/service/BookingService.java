package com.hotel.booking.service;

import com.hotel.booking.dto.BookingDetailResponseDto;
import com.hotel.booking.dto.BookingRequestDto;
import com.hotel.booking.dto.GuestResponseDto;
import com.hotel.booking.dto.RoomSummaryResponseDto;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Guest;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.*;
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

//        Setting Confirmation number
        booking.setConfirmationNumber(UUID.randomUUID().toString());

        booking.setGuest(guest);
        booking.setRoom(room);

        room.setIsAvailable(false);
        roomRepository.save(room);

        Booking savedBooking = bookingRepository.save(booking);

        BookingDetailResponseDto dto = new BookingDetailResponseDto();
        dto.setId(savedBooking.getId());
        dto.setCheckInDate(savedBooking.getCheckInDate());
        dto.setCheckOutDate(savedBooking.getCheckOutDate());
        dto.setStatus(savedBooking.getStatus());
        dto.setConfirmationNumber(savedBooking.getConfirmationNumber());

        GuestResponseDto guestDto = new GuestResponseDto();
        guestDto.setId(guest.getId());
        guestDto.setName(guest.getName());
        guestDto.setEmail(guest.getEmail());
        guestDto.setPhone(guest.getPhone());
        dto.setGuest(guestDto);

        RoomSummaryResponseDto roomDto = new RoomSummaryResponseDto();
        roomDto.setId(room.getId());
        roomDto.setRoomNumber(room.getRoomNumber());
        roomDto.setType(room.getType());
        roomDto.setPricePerNight(room.getPricePerNight());
        roomDto.setIsAvailable(room.getIsAvailable());
        dto.setRoom(roomDto);

        return dto;
    }

    public BookingDetailResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        BookingDetailResponseDto dto = new BookingDetailResponseDto();
        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setStatus(booking.getStatus());
        dto.setConfirmationNumber(booking.getConfirmationNumber());

        GuestResponseDto guestDto = new GuestResponseDto();
        guestDto.setId(booking.getGuest().getId());
        guestDto.setName(booking.getGuest().getName());
        guestDto.setEmail(booking.getGuest().getEmail());
        guestDto.setPhone(booking.getGuest().getPhone());
        dto.setGuest(guestDto);

        RoomSummaryResponseDto roomDto = new RoomSummaryResponseDto();
        roomDto.setId(booking.getRoom().getId());
        roomDto.setRoomNumber(booking.getRoom().getRoomNumber());
        roomDto.setType(booking.getRoom().getType());
        roomDto.setPricePerNight(booking.getRoom().getPricePerNight());
        roomDto.setIsAvailable(booking.getRoom().getIsAvailable());
        dto.setRoom(roomDto);

        return dto;
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

        BookingDetailResponseDto dto = new BookingDetailResponseDto();
        dto.setId(updatedBooking.getId());
        dto.setCheckInDate(updatedBooking.getCheckInDate());
        dto.setCheckOutDate(updatedBooking.getCheckOutDate());
        dto.setStatus(updatedBooking.getStatus());

        GuestResponseDto guestDto = new GuestResponseDto();
        guestDto.setId(updatedBooking.getGuest().getId());
        guestDto.setName(updatedBooking.getGuest().getName());
        guestDto.setEmail(updatedBooking.getGuest().getEmail());
        guestDto.setPhone(updatedBooking.getGuest().getPhone());
        dto.setGuest(guestDto);

        RoomSummaryResponseDto roomDto = new RoomSummaryResponseDto();
        roomDto.setId(room.getId());
        roomDto.setRoomNumber(room.getRoomNumber());
        roomDto.setType(room.getType());
        roomDto.setPricePerNight(room.getPricePerNight());
        roomDto.setIsAvailable(room.getIsAvailable());
        dto.setRoom(roomDto);

        return dto;
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
