package com.hotel.booking.mapper;

import com.hotel.booking.dto.GuestRequestDto;
import com.hotel.booking.dto.GuestResponseDto;
import com.hotel.booking.dto.HotelSummaryResponseDto;
import com.hotel.booking.entity.Guest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuestMapper {
    GuestResponseDto toResponseDto(Guest guest);
    Guest toEntity(GuestRequestDto dto);
}
