package com.hotel.booking.dto;

import lombok.Data;

@Data
public class HotelSummaryResponseDto {
    private Long id;
    private String name;
    private String city;
    private int starRating;
}