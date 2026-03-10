package com.hotel.booking.dto;

import com.hotel.booking.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoomRequestDto {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType type;

    @NotNull(message = "Price per night is required")
    @Positive(message = "Price per night must be greater than 0")
    private Double pricePerNight;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
}