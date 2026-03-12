package com.hotel.booking.mapper;

import com.hotel.booking.dto.HotelDetailResponseDto;
import com.hotel.booking.dto.HotelRequestDto;
import com.hotel.booking.dto.HotelStatsResponseDto;
import com.hotel.booking.dto.HotelSummaryResponseDto;
import com.hotel.booking.entity.Hotel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoomMapper.class})
public interface HotelMapper {
    HotelDetailResponseDto toDetailDto(Hotel hotel);
    HotelSummaryResponseDto toSummaryDto(Hotel hotel);
    HotelStatsResponseDto toStatsDto(Hotel hotel);
    Hotel toEntity(HotelRequestDto dto);
}
