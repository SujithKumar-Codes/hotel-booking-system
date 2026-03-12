package com.hotel.booking.service;

import com.hotel.booking.dto.*;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.exception.HotelNotFoundException;
import com.hotel.booking.mapper.HotelMapper;
import com.hotel.booking.mapper.RoomMapper;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelMapper hotelMapper;


    public Page<HotelSummaryResponseDto> getAllHotels(Pageable pageable) {
        return hotelRepository.findAll(pageable)
                .map(hotel -> {
                    HotelSummaryResponseDto dto = hotelMapper.toSummaryDto(hotel);
                    return dto;
                });
    }


    public HotelDetailResponseDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + id));
        HotelDetailResponseDto dto = hotelMapper.toDetailDto(hotel);
        return dto;
    }


    public HotelDetailResponseDto createHotel(HotelRequestDto requestDto) {
        Hotel hotel = hotelMapper.toEntity(requestDto);
        Hotel savedHotel = hotelRepository.save(hotel);
        HotelDetailResponseDto dto = hotelMapper.toDetailDto(savedHotel);
        return dto;
    }

    public HotelDetailResponseDto updateHotel(Long id, HotelRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + id));

        hotel.setName(requestDto.getName());
        hotel.setCity(requestDto.getCity());
        hotel.setStarRating(requestDto.getStarRating());

        Hotel updatedHotel = hotelRepository.save(hotel);
        HotelDetailResponseDto dto = hotelMapper.toDetailDto(updatedHotel);
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