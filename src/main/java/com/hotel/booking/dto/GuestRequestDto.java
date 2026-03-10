package com.hotel.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuestRequestDto {
    @NotBlank(message = "Name must be filled")
    private String name;

    @Email(message = "Must enter a valid Email id")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;
}