
package com.meetingroom.booking.controller;

import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.entity.MeetingRoom;
import com.meetingroom.booking.service.BookingService;
import com.meetingroom.booking.service.MeetingRoomService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/timeline")
public class AdminTimelineController {

    private final BookingService bookingService;
    private final MeetingRoomService meetingRoomService;

    private static final LocalTime START_HOUR =
            LocalTime.of(7, 0);

    private static final LocalTime END_HOUR =
            LocalTime.of(18, 0);

    private static final int TOTAL_MINUTES = 660;

    public AdminTimelineController(
            BookingService bookingService,
            MeetingRoomService meetingRoomService) {
        this.bookingService = bookingService;
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping
    public String timeline(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            Long roomId,

            Model model) {

        LocalDate selectedDate = date != null
                ? date
                : LocalDate.now();

        LocalDateTime dayStart =
                selectedDate.atStartOfDay();

        LocalDateTime dayEnd =
                selectedDate.plusDays(1).atStartOfDay();

        // Booking pada tanggal terpilih
        List<Booking> bookings =
                bookingService.getBookingsByWeek(dayStart, dayEnd);

        // Ruangan
        List<MeetingRoom> rooms =
                meetingRoomService.getAllRooms();

        // Filter ruangan
        if (roomId != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getRoomId().equals(roomId))
                    .toList();

            bookings = bookings.stream()
                    .filter(booking ->
                            booking.getMeetingRoom() != null
                            && booking.getMeetingRoom()
                                    .getRoomId().equals(roomId))
                    .toList();
        }

        List<TimelineRoom> timelineRooms =
                new ArrayList<>();

        for (MeetingRoom room : rooms) {

            List<TimelineBooking> roomBookings =
                    new ArrayList<>();

            for (Booking booking : bookings) {

                if (booking.getMeetingRoom() == null
                        || !booking.getMeetingRoom()
                                .getRoomId().equals(room.getRoomId())) {
                    continue;
                }

                LocalDateTime start = booking.getStartTime();
                LocalDateTime end = booking.getEndTime();

                // Batasi tampilan pada jam 07.00 - 18.00
                LocalDateTime visibleStart =
                        selectedDate.atTime(START_HOUR);

                LocalDateTime visibleEnd =
                        selectedDate.atTime(END_HOUR);

                if (!start.isBefore(visibleEnd)
                        || !end.isAfter(visibleStart)) {
                    continue;
                }

                LocalDateTime clippedStart =
                        start.isBefore(visibleStart)
                                ? visibleStart
                                : start;

                LocalDateTime clippedEnd =
                        end.isAfter(visibleEnd)
                                ? visibleEnd
                                : end;

                int startMinutes =
                        (int) java.time.Duration.between(
                                visibleStart,
                                clippedStart
                        ).toMinutes();

                int durationMinutes =
                        (int) java.time.Duration.between(
                                clippedStart,
                                clippedEnd
                        ).toMinutes();

                double left =
                        startMinutes * 100.0 / TOTAL_MINUTES;

                double width =
                        durationMinutes * 100.0 / TOTAL_MINUTES;

                roomBookings.add(
                        new TimelineBooking(
                                booking,
                                left,
                                width
                        )
                );
            }

            timelineRooms.add(
                    new TimelineRoom(
                            room,
                            roomBookings
                    )
            );
        }

        model.addAttribute("timelineRooms", timelineRooms);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute(
                "rooms",
                meetingRoomService.getAllRooms()
        );

        model.addAttribute(
                "previousDate",
                selectedDate.minusDays(1)
        );

        model.addAttribute(
                "nextDate",
                selectedDate.plusDays(1)
        );

        model.addAttribute("startHour", START_HOUR);
        model.addAttribute("endHour", END_HOUR);

        return "admin/timeline";
    }

    public record TimelineRoom(
            MeetingRoom room,
            List<TimelineBooking> bookings) {
    }

    public record TimelineBooking(
            Booking booking,
            double left,
            double width) {
    }
}
