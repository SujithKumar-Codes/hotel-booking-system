package com.hotel.booking.service;

import com.hotel.booking.dto.*;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.exception.HotelNotFoundException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
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

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

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


//    BONUS (Hotel stats)
    public HotelStatsResponseDto getHotelStats(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        Long totalRooms = roomRepository.countByHotel(hotel);
        Long activeBookings = bookingRepository.countByRoomHotelAndStatus(hotel, BookingStatus.CONFIRMED);
        Double revenue = bookingRepository.calculateRevenueThisMonth(hotel);

        HotelStatsResponseDto dto = new HotelStatsResponseDto();
        dto.setTotalRooms(totalRooms);
        dto.setActiveBookings(activeBookings);
        dto.setRevenueThisMonth(revenue != null ? revenue : 0.0);

        return dto;
    }

}
