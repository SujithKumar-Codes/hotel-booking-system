package com.hotel.booking.dto;

import com.hotel.booking.entity.RoomType;
import lombok.Data;

@Data
public class RoomSummaryResponseDto {
    private Long id;
    private String roomNumber;
    private RoomType type;
    private Double pricePerNight;
    private Boolean isAvailable;
}