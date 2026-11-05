
package com.meetingroom.booking.controller;

import com.meetingroom.booking.service.BookingService;
import com.meetingroom.booking.service.MeetingRoomService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.meetingroom.booking.entity.Booking;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final BookingService bookingService;
    private final MeetingRoomService meetingRoomService;

    public AdminDashboardController(
            BookingService bookingService,
            MeetingRoomService meetingRoomService) {

        this.bookingService = bookingService;
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Statistik dashboard
        model.addAttribute(
                "totalBookings",
                bookingService.countAllBookings()
        );

        model.addAttribute(
                "confirmedBookings",
                bookingService.countConfirmedBookings()
        );

        model.addAttribute(
                "cancelledBookings",
                bookingService.countCancelledBookings()
        );

        model.addAttribute(
                "activeRooms",
                meetingRoomService.countActiveRooms()
        );

        // Booking terbaru
        model.addAttribute(
                "recentBookings",
                bookingService.getRecentBookings()
        );

        // Monitoring meeting hari ini
        model.addAttribute(
                "todayBookings",
                bookingService.getTodayBookings()
        );
        
        
        List<Booking> todayBookings = bookingService.getTodayBookings();
        
        // Booking yang statusnya masih CONFIRMED
        List<Booking> confirmedToday = todayBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .toList();
        
        // Meeting yang sedang berlangsung
        List<Booking> ongoingMeetings = confirmedToday.stream()
                .filter(b -> !b.getStartTime().isAfter(now)
                        && b.getEndTime().isAfter(now))
                .toList();
        
        // Meeting berikutnya
        List<Booking> upcomingMeetings = confirmedToday.stream()
                .filter(b -> b.getStartTime().isAfter(now))
                .toList();
        
        // Ruangan yang sedang digunakan
        Set<Long> occupiedRoomIds = ongoingMeetings.stream()
                .map(b -> b.getMeetingRoom().getRoomId())
                .collect(Collectors.toSet());
        
        // Statistik monitoring
        model.addAttribute("todayBookingCount", todayBookings.size());
        model.addAttribute("ongoingMeetingCount", ongoingMeetings.size());
        model.addAttribute("nextMeeting", upcomingMeetings.isEmpty()
                ? null : upcomingMeetings.get(0));
        model.addAttribute("occupiedRoomCount", occupiedRoomIds.size());


        // Informasi waktu
        model.addAttribute("today", today);

        model.addAttribute("formattedDate",
                today.format(
                        DateTimeFormatter.ofPattern(
                                "EEEE, dd MMMM yyyy",
                                new Locale("id", "ID")
                        )
                )
        );

        model.addAttribute("now", now);

        return "admin/dashboard";
    }
}
