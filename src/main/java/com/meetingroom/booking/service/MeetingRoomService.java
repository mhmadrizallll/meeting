package com.meetingroom.booking.service;

import com.meetingroom.booking.entity.MeetingRoom;
import com.meetingroom.booking.repository.MeetingRoomRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomService(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Transactional(readOnly = true)
    public List<MeetingRoom> getActiveRooms() {
        return meetingRoomRepository.findByStatusOrderByRoomNameAsc("ACTIVE");
    }

    @Transactional(readOnly = true)
    public List<MeetingRoom> getAllRooms() {
        return meetingRoomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MeetingRoom getRoomById(Long roomId) {
        return meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ruangan tidak ditemukan dengan ID: " + roomId
                ));
    }

    @Transactional
    public MeetingRoom saveRoom(MeetingRoom room) {

        String roomName = room.getRoomName().trim();
        room.setRoomName(roomName);

        boolean duplicate;

        if (room.getRoomId() == null) {
            duplicate = meetingRoomRepository
                    .existsByRoomNameIgnoreCase(roomName);
        } else {
            duplicate = meetingRoomRepository
                    .existsByRoomNameIgnoreCaseAndRoomIdNot(
                            roomName,
                            room.getRoomId()
                    );
        }

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Nama ruangan sudah digunakan."
            );
        }

        if (room.getRoomId() == null) {
            room.setStatus("ACTIVE");
        }

        return meetingRoomRepository.save(room);
    }

    @Transactional
    public void deactivateRoom(Long roomId) {
        MeetingRoom room = getRoomById(roomId);
        room.setStatus("INACTIVE");
        meetingRoomRepository.save(room);
    }

    @Transactional
    public void activateRoom(Long roomId) {
        MeetingRoom room = getRoomById(roomId);
        room.setStatus("ACTIVE");
        meetingRoomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public long countActiveRooms() {
        return meetingRoomRepository.countByStatus("ACTIVE");
    }
}