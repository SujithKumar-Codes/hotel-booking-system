package com.hotel.booking.service;

import com.hotel.booking.dto.RoomRequestDto;
import com.hotel.booking.dto.RoomSummaryResponseDto;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.HotelNotFoundException;
import com.hotel.booking.exception.RoomNotFoundException;
import com.hotel.booking.mapper.RoomMapper;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomMapper roomMapper;


    public Page<RoomSummaryResponseDto> getRoomsByHotel(Long hotelId, Pageable pageable) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        return roomRepository.findByHotel(hotel, pageable)
                .map(room -> roomMapper.toSummaryDto(room));
    }

    public List<RoomSummaryResponseDto> getAvailableRooms(String city, LocalDate date) {
        return roomRepository.findAvailableRoomsByCityAndDate(city, date)
                .stream()
                .map(room -> roomMapper.toSummaryDto(room))
                .toList();
    }

    public RoomSummaryResponseDto addRoomToHotel(Long hotelId, RoomRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        Room room = roomMapper.toEntity(requestDto);
        room.setHotel(hotel);

        Room savedRoom = roomRepository.save(room);

        return roomMapper.toSummaryDto(savedRoom);
    }

    public RoomSummaryResponseDto updateRoom(Long id, RoomRequestDto requestDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));

        room.setRoomNumber(requestDto.getRoomNumber());
        room.setType(requestDto.getType());
        room.setPricePerNight(requestDto.getPricePerNight());
        room.setIsAvailable(requestDto.getIsAvailable());

        Room updatedRoom = roomRepository.save(room);

        return roomMapper.toSummaryDto(updatedRoom);
    }

    public void deleteRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with the id: " + id));

        roomRepository.delete(room);
    }


    //    BONUS
    public List<RoomSummaryResponseDto> getRoomsByPriceRange(Double minPrice, Double maxPrice) {
        return roomRepository.findByPricePerNightBetween(minPrice, maxPrice)
                .stream()
                .map(room -> roomMapper.toSummaryDto(room))
                .toList();
    }

}