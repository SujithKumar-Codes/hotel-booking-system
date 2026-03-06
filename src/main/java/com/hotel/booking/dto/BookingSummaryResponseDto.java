package com.hotel.booking.dto;

import com.hotel.booking.entity.BookingStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingSummaryResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
}