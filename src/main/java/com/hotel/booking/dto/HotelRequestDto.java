package com.hotel.booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HotelRequestDto {
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "City must not be blank")
    private String city;
    @Min(1) @Max(5)
    private int starRating;
}