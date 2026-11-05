
package com.meetingroom.booking.controller;

import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PublicScheduleController {

    private final BookingService bookingService;

    public PublicScheduleController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String todaySchedule(Model model) {
        List<Booking> bookings = bookingService.getTodayBookings();

        model.addAttribute("bookings", bookings);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute(
            "formattedDate",
            LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", 
                    new java.util.Locale("id", "ID"))
            )
        );

        model.addAttribute("totalBookings", bookings.size());

        return "public/schedule";
    }
}
