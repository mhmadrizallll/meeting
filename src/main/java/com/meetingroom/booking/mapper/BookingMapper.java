package com.meetingroom.booking.mapper;

import com.meetingroom.booking.dto.BookingRequest;
import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.entity.MeetingRoom;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequest request) {

        Booking booking = new Booking();

        MeetingRoom room = new MeetingRoom();
        room.setRoomId(request.getRoomId());

        booking.setMeetingRoom(room);
        booking.setBookerName(request.getBookerName());
        booking.setEmail(request.getEmail());
        booking.setPhone(request.getPhone());
        booking.setDepartment(request.getDepartment());
        booking.setAgenda(request.getAgenda());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());

        return booking;
    }
}