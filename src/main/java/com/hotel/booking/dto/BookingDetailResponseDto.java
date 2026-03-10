package com.hotel.booking.dto;

import com.hotel.booking.entity.BookingStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDetailResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private String confirmationNumber;
    private GuestResponseDto guest;
    private RoomSummaryResponseDto room;
}