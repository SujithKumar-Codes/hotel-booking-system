package com.hotel.booking.repository;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.BookingStatus;
import com.hotel.booking.entity.Guest;
import com.hotel.booking.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByGuest(Guest guest, Pageable pageable);

//    BONUS
    Long countByRoomHotelAndStatus(Hotel hotel, BookingStatus status);

    @Query("SELECT SUM(r.pricePerNight * (DAY(b.checkOutDate) - DAY(b.checkInDate))) " +
            "FROM Booking b JOIN b.room r " +
            "WHERE b.room.hotel = :hotel " +
            "AND b.status = 'CONFIRMED' " +
            "AND MONTH(b.checkInDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(b.checkInDate) = YEAR(CURRENT_DATE)")

    Double calculateRevenueThisMonth(@Param("hotel") Hotel hotel);
}
