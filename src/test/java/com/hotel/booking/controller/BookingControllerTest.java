package com.hotel.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dto.BookingRequestDto;
import com.hotel.booking.dto.GuestRequestDto;
import com.hotel.booking.dto.HotelRequestDto;
import com.hotel.booking.dto.RoomRequestDto;
import com.hotel.booking.entity.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//Testing should be done against H2 dev database
@ActiveProfiles("dev")
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper method to create a hotel and return its ID
    private Long createHotel() throws Exception {
        HotelRequestDto dto = new HotelRequestDto();
        dto.setName("Taj Hotel");
        dto.setCity("Mumbai");
        dto.setStarRating(5);

        MvcResult result = mockMvc.perform(post("/api/hotels")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    // Helper method to create a room and return its ID
    private Long createRoom(Long hotelId) throws Exception {
        RoomRequestDto dto = new RoomRequestDto();
        dto.setRoomNumber("101");
        dto.setType(RoomType.SINGLE);
        dto.setPricePerNight(1500.0);
        dto.setIsAvailable(true);

        MvcResult result = mockMvc.perform(post("/api/hotels/" + hotelId + "/rooms")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    // Helper method to create a guest and return its ID
    private Long createGuest() throws Exception {
        GuestRequestDto dto = new GuestRequestDto();
        dto.setName("John Doe");
        dto.setEmail("john@gmail.com");
        dto.setPhone("9876543210");

        MvcResult result = mockMvc.perform(post("/api/guests")
                        .with(httpBasic("guest", "guest123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    // Helper method to create a booking and return its ID
    private Long createBooking(Long guestId, Long roomId) throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));
        dto.setGuestId(guestId);
        dto.setRoomId(roomId);

        MvcResult result = mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("guest", "guest123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    // Full end-to-end flow
    @Test
    void fullBookingFlow() throws Exception {
        Long hotelId = createHotel();
        Long roomId = createRoom(hotelId);
        Long guestId = createGuest();
        Long bookingId = createBooking(guestId, roomId);

        // Get booking details
        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .with(httpBasic("guest", "guest123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Cancel booking
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel")
                        .with(httpBasic("guest", "guest123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    // Book already booked room → 409
    @Test
    void createBooking_RoomAlreadyBooked() throws Exception {
        Long hotelId = createHotel();
        Long roomId = createRoom(hotelId);
        Long guestId = createGuest();
        createBooking(guestId, roomId);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));
        dto.setGuestId(guestId);
        dto.setRoomId(roomId);

        mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("guest", "guest123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    // checkOut before checkIn → 400
    @Test
    void createBooking_InvalidDateRange() throws Exception {
        Long hotelId = createHotel();
        Long roomId = createRoom(hotelId);
        Long guestId = createGuest();

        BookingRequestDto dto = new BookingRequestDto();
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(1));
        dto.setGuestId(guestId);
        dto.setRoomId(roomId);

        mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("guest", "guest123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // Book with non-existent guest → 404
    @Test
    void createBooking_GuestNotFound() throws Exception {
        Long hotelId = createHotel();
        Long roomId = createRoom(hotelId);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));
        dto.setGuestId(999L);
        dto.setRoomId(roomId);

        mockMvc.perform(post("/api/bookings")
                        .with(httpBasic("guest", "guest123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // Cancel already cancelled booking → 400
    @Test
    void cancelBooking_AlreadyCancelled() throws Exception {
        Long hotelId = createHotel();
        Long roomId = createRoom(hotelId);
        Long guestId = createGuest();
        Long bookingId = createBooking(guestId, roomId);

        // Cancel once
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel")
                        .with(httpBasic("guest", "guest123")))
                .andExpect(status().isOk());

        // Cancel again → 400
        mockMvc.perform(put("/api/bookings/" + bookingId + "/cancel")
                        .with(httpBasic("guest", "guest123")))
                .andExpect(status().isBadRequest());
    }
}