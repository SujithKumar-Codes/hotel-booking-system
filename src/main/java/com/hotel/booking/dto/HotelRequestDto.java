package com.hotel.booking.dto;

import lombok.Data;

@Data
public class HotelRequestDto {
    private String name;
    private String city;
    private int starRating;
}