package com.hotel.booking.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequestDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long guestId;
    private Long roomId;
}