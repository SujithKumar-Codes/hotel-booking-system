package com.hotel.booking.service;

import com.hotel.booking.dto.BookingDetailResponseDto;
import com.hotel.booking.dto.BookingRequestDto;
import com.hotel.booking.entity.*;
import com.hotel.booking.exception.*;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.GuestRepository;
import com.hotel.booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Guest guest;
    private Room room;
    private Hotel hotel;
    private BookingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Taj Hotel");
        hotel.setCity("Mumbai");
        hotel.setStarRating(5);

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setType(RoomType.SINGLE);
        room.setPricePerNight(1500.0);
        room.setIsAvailable(true);
        room.setHotel(hotel);

        guest = new Guest();
        guest.setId(1L);
        guest.setName("Sujith Kumar");
        guest.setEmail("kumarsujith21911@gmail.com");
        guest.setPhone("9876543210");

        requestDto = new BookingRequestDto();
        requestDto.setCheckInDate(LocalDate.now().plusDays(1));
        requestDto.setCheckOutDate(LocalDate.now().plusDays(5));
        requestDto.setGuestId(1L);
        requestDto.setRoomId(1L);
    }

    // Happy path
    @Test
    void createBooking_Success() {
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookingDetailResponseDto result = bookingService.createBooking(requestDto);

        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        assertNotNull(result.getConfirmationNumber());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // Room not available → 409
    @Test
    void createBooking_RoomNotAvailable() {
        room.setIsAvailable(false);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomNotAvailableException ex = assertThrows(RoomNotAvailableException.class,
                () -> bookingService.createBooking(requestDto));

        assertEquals("Room is not available for the requested dates", ex.getMessage());
    }

    // Invalid date range → 400
    @Test
    void createBooking_InvalidDateRange() {
        requestDto.setCheckOutDate(LocalDate.now().plusDays(1));
        requestDto.setCheckInDate(LocalDate.now().plusDays(5));

        InvalidDateRangeException ex = assertThrows(InvalidDateRangeException.class,
                () -> bookingService.createBooking(requestDto));

        assertEquals("Check-out date must be after check-in date", ex.getMessage());
    }

    // Guest not found → 404
    @Test
    void createBooking_GuestNotFound() {
        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        GuestNotFoundException ex = assertThrows(GuestNotFoundException.class,
                () -> bookingService.createBooking(requestDto));

        assertEquals("Guest not found with id: 1", ex.getMessage());
    }

    // Cancel booking happy path
    @Test
    void cancelBooking_Success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDetailResponseDto result = bookingService.cancelBooking(1L);

        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // Cancel already cancelled booking → 400
    @Test
    void cancelBooking_AlreadyCancelled() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setGuest(guest);
        booking.setRoom(room);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        InvalidDateRangeException ex = assertThrows(InvalidDateRangeException.class,
                () -> bookingService.cancelBooking(1L));

        assertEquals("Booking is already cancelled", ex.getMessage());
    }
}