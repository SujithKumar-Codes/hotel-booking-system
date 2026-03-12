package com.hotel.booking.mapper;

import com.hotel.booking.dto.BookingDetailResponseDto;
import com.hotel.booking.dto.BookingRequestDto;
import com.hotel.booking.dto.BookingSummaryResponseDto;
import com.hotel.booking.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {GuestMapper.class, RoomMapper.class})
public interface BookingMapper {
    BookingDetailResponseDto toDetailDto(Booking booking);
    BookingSummaryResponseDto toSummaryDto(Booking booking);
    Booking toEntity(BookingRequestDto dto);
}