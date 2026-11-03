
package com.meetingroom.booking.repository;

import com.meetingroom.booking.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    // Mencari booking berdasarkan kode
    Optional<Booking> findByBookingCode(String bookingCode);

    // Mencari booking berdasarkan email
    List<Booking> findByEmailIgnoreCaseOrderByStartTimeDesc(String email);

    // Mencari booking berdasarkan ruangan
    List<Booking> findByMeetingRoomRoomIdOrderByStartTimeAsc(Long roomId);

    // Mencari seluruh booking berdasarkan status
    List<Booking> findByStatusOrderByStartTimeAsc(String status);

    // Mencari booking pada tanggal tertentu
    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.startTime < :dayEnd
          AND b.endTime > :dayStart
        ORDER BY b.startTime ASC
    """)
    List<Booking> findBookingsByDate(
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );

    // Memeriksa apakah jadwal ruangan bentrok
    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.meetingRoom.roomId = :roomId
          AND b.status = 'CONFIRMED'
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    long countConflictingBookings(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // Mencari booking berdasarkan kode dan email
    Optional<Booking> findByBookingCodeAndEmailIgnoreCase(
            String bookingCode,
            String email
    );
    
    long countByStatus(String status);

    long countByStatusNot(String status);
    
    List<Booking> findTop10ByOrderByCreatedAtDesc();
}
