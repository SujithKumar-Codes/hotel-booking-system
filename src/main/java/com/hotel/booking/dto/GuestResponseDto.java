package com.hotel.booking.dto;

import lombok.Data;

@Data
public class GuestResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
}