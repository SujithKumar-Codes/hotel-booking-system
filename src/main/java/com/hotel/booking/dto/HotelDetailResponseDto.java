package com.hotel.booking.dto;

import lombok.Data;
import java.util.List;

@Data
public class HotelDetailResponseDto {
    private Long id;
    private String name;
    private String city;
    private int starRating;
    private List<RoomSummaryResponseDto> rooms;
}