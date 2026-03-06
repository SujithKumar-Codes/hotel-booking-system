package com.hotel.booking.service;

import com.hotel.booking.dto.RoomRequestDto;
import com.hotel.booking.dto.RoomSummaryResponseDto;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.HotelNotFoundException;
import com.hotel.booking.exception.RoomNotFoundException;
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

    public Page<RoomSummaryResponseDto> getRoomsByHotel(Long hotelId, Pageable pageable) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        return roomRepository.findByHotel(hotel, pageable)
                .map(room -> {
                    RoomSummaryResponseDto dto = new RoomSummaryResponseDto();
                    dto.setId(room.getId());
                    dto.setRoomNumber(room.getRoomNumber());
                    dto.setType(room.getType());
                    dto.setPricePerNight(room.getPricePerNight());
                    dto.setIsAvailable(room.getIsAvailable());
                    return dto;
                });
    }

    public List<RoomSummaryResponseDto> getAvailableRooms(String city, LocalDate date) {
        return roomRepository.findAvailableRoomsByCityAndDate(city, date)
                .stream()
                .map(room -> {
                    RoomSummaryResponseDto dto = new RoomSummaryResponseDto();
                    dto.setId(room.getId());
                    dto.setRoomNumber(room.getRoomNumber());
                    dto.setType(room.getType());
                    dto.setPricePerNight(room.getPricePerNight());
                    dto.setIsAvailable(room.getIsAvailable());
                    return dto;
                }).toList();
    }

    public RoomSummaryResponseDto addRoomToHotel(Long hotelId, RoomRequestDto requestDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        Room room = new Room();
        room.setRoomNumber(requestDto.getRoomNumber());
        room.setType(requestDto.getType());
        room.setPricePerNight(requestDto.getPricePerNight());
        room.setIsAvailable(requestDto.getIsAvailable());
        room.setHotel(hotel);

        Room savedRoom = roomRepository.save(room);

        RoomSummaryResponseDto dto = new RoomSummaryResponseDto();
        dto.setId(savedRoom.getId());
        dto.setRoomNumber(savedRoom.getRoomNumber());
        dto.setType(savedRoom.getType());
        dto.setPricePerNight(savedRoom.getPricePerNight());
        dto.setIsAvailable(savedRoom.getIsAvailable());

        return dto;
    }

    public RoomSummaryResponseDto updateRoom(Long id, RoomRequestDto requestDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));

        room.setRoomNumber(requestDto.getRoomNumber());
        room.setType(requestDto.getType());
        room.setPricePerNight(requestDto.getPricePerNight());
        room.setIsAvailable(requestDto.getIsAvailable());

        Room updatedRoom = roomRepository.save(room);

        RoomSummaryResponseDto dto = new RoomSummaryResponseDto();
        dto.setId(updatedRoom.getId());
        dto.setRoomNumber(updatedRoom.getRoomNumber());
        dto.setType(updatedRoom.getType());
        dto.setPricePerNight(updatedRoom.getPricePerNight());
        dto.setIsAvailable(updatedRoom.getIsAvailable());

        return dto;
    }

    public void deleteRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with the id: " + id));

        roomRepository.delete(room);
    }

}
