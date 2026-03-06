package com.hotel.booking.service;

import com.hotel.booking.dto.HotelDetailResponseDto;
import com.hotel.booking.dto.HotelRequestDto;
import com.hotel.booking.dto.HotelSummaryResponseDto;
import com.hotel.booking.dto.RoomSummaryResponseDto;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.exception.HotelNotFoundException;
import com.hotel.booking.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public Page<HotelSummaryResponseDto> getAllHotels(Pageable pageable) {
        return hotelRepository.findAll(pageable)
                .map(hotel -> {
                    HotelSummaryResponseDto dto = new HotelSummaryResponseDto();
                    dto.setId(hotel.getId());
                    dto.setName(hotel.getName());
                    dto.setCity(hotel.getCity());
                    dto.setStarRating(hotel.getStarRating());
                    return dto;
                });
    }


    public HotelDetailResponseDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + id));

        HotelDetailResponseDto dto = new HotelDetailResponseDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setCity(hotel.getCity());
        dto.setStarRating(hotel.getStarRating());
        dto.setRooms(hotel.getRooms().stream()
                .map(room -> {
                    RoomSummaryResponseDto roomDto = new RoomSummaryResponseDto();
                    roomDto.setId(room.getId());
                    roomDto.setRoomNumber(room.getRoomNumber());
                    roomDto.setType(room.getType());
                    roomDto.setPricePerNight(room.getPricePerNight());
                    roomDto.setIsAvailable(room.getIsAvailable());
                    return roomDto;
                }).toList());

        return dto;
    }


    public HotelDetailResponseDto createHotel(HotelRequestDto requestDto) {
        Hotel hotel = new Hotel();
        hotel.setName(requestDto.getName());
        hotel.setCity(requestDto.getCity());
        hotel.setStarRating(requestDto.getStarRating());

        Hotel savedHotel = hotelRepository.save(hotel);

        HotelDetailResponseDto dto = new HotelDetailResponseDto();
        dto.setId(savedHotel.getId());
        dto.setName(savedHotel.getName());
        dto.setCity(savedHotel.getCity());
        dto.setStarRating(savedHotel.getStarRating());
        dto.setRooms(new ArrayList<>());

        return dto;
    }

    public HotelDetailResponseDto updateHotel(Long id, HotelRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + id));

        hotel.setName(requestDto.getName());
        hotel.setCity(requestDto.getCity());
        hotel.setStarRating(requestDto.getStarRating());

        Hotel updatedHotel = hotelRepository.save(hotel);

        HotelDetailResponseDto dto = new HotelDetailResponseDto();
        dto.setId(updatedHotel.getId());
        dto.setName(updatedHotel.getName());
        dto.setCity(updatedHotel.getCity());
        dto.setStarRating(updatedHotel.getStarRating());
        dto.setRooms(updatedHotel.getRooms().stream()
                .map(room -> {
                    RoomSummaryResponseDto roomDto = new RoomSummaryResponseDto();
                    roomDto.setId(room.getId());
                    roomDto.setRoomNumber(room.getRoomNumber());
                    roomDto.setType(room.getType());
                    roomDto.setPricePerNight(room.getPricePerNight());
                    roomDto.setIsAvailable(room.getIsAvailable());
                    return roomDto;
                }).toList());

        return dto;
    }

    public void deleteHotel(Long id){
        Hotel hotel = hotelRepository.findById(id)
                        .orElseThrow(() -> new HotelNotFoundException("Hotel not found with the id: " + id));
        hotelRepository.delete(hotel);
    }

}
