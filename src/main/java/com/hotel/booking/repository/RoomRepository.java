package com.hotel.booking.repository;

import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findByHotel(Hotel hotel, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.hotel.city = :city AND r.isAvailable = true AND r.id NOT IN " +
            "(SELECT b.room.id FROM Booking b WHERE b.checkInDate <= :date AND b.checkOutDate > :date)")
    List<Room> findAvailableRoomsByCityAndDate(@Param("city") String city, @Param("date") LocalDate date);
}
