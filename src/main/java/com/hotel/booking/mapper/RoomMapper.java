package com.hotel.booking.mapper;

import com.hotel.booking.dto.RoomRequestDto;
import com.hotel.booking.dto.RoomSummaryResponseDto;
import com.hotel.booking.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomSummaryResponseDto toSummaryDto(Room room);
    Room toEntity(RoomRequestDto dto);
}
