
package com.meetingroom.booking.controller;

import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.service.BookingService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Daftar semua booking
    @GetMapping
    public String index(
            @RequestParam(required = false) String status,
            Model model
    ) {
        List<Booking> bookings = bookingService.getAllBookings();

        if (status != null && !status.isBlank()) {
            bookings = bookings.stream()
                    .filter(b -> status.equalsIgnoreCase(b.getStatus()))
                    .toList();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedStatus", status);

        return "admin/bookings/index";
    }

    // Detail booking
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {
        Booking booking = bookingService.getBookingById(id);

        model.addAttribute("booking", booking);

        return "admin/bookings/detail";
    }

    // Batalkan booking
    @PostMapping("/{id}/cancel")
    public String cancel(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.cancelBookingByAdmin(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Booking berhasil dibatalkan."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/bookings";
    }

    // Tandai booking selesai
    @PostMapping("/{id}/complete")
    public String complete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.completeBooking(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Booking berhasil ditandai selesai."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/bookings";
    }
}
