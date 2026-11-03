package com.meetingroom.booking.controller;

import com.meetingroom.booking.service.BookingService;
import com.meetingroom.booking.service.MeetingRoomService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        model.addAttribute(
                "recentBookings",
                bookingService.getRecentBookings()
        );

        return "admin/dashboard";
    }
}