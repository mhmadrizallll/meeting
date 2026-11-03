
package com.meetingroom.booking.repository;

import com.meetingroom.booking.entity.MeetingRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface MeetingRoomRepository
        extends JpaRepository<MeetingRoom, Long> {

    // Menampilkan seluruh ruangan aktif
    List<MeetingRoom> findByStatusOrderByRoomNameAsc(String status);

    // Mencari ruangan berdasarkan nama
    boolean existsByRoomNameIgnoreCase(String roomName);

    // Mencari ruangan berdasarkan status
    List<MeetingRoom> findByStatus(String status);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM MeetingRoom r WHERE r.roomId = :roomId")
    Optional<MeetingRoom> findByIdForUpdate(
        @Param("roomId") Long roomId
    );
    
    long countByStatus(String status);
    
    boolean existsByRoomNameIgnoreCaseAndRoomIdNot(
        String roomName,
        Long roomId
    );
}
