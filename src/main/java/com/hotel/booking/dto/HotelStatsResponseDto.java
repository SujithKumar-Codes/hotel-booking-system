package com.hotel.booking.dto;

import lombok.Data;

@Data
public class HotelStatsResponseDto {
    private Long totalRooms;
    private Long activeBookings;
    private Double revenueThisMonth;
}