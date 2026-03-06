package com.hotel.booking.dto;

import lombok.Data;

@Data
public class GuestRequestDto {
    private String name;
    private String email;
    private String phone;
}