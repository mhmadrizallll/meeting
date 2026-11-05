
package com.meetingroom.booking.controller;

import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.service.BookingService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/booking/lookup")
public class BookingLookupController {

    private final BookingService bookingService;

    public BookingLookupController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Halaman pencarian booking
    @GetMapping
    public String lookupPage() {
        return "public/booking-lookup";
    }

    // Cari booking
    @PostMapping
    public String search(
            @RequestParam String bookingCode,
            @RequestParam String email,
            Model model
    ) {

        if (bookingCode.isBlank() || email.isBlank()) {
            model.addAttribute(
                    "errorMessage",
                    "Kode booking dan email wajib diisi."
            );

            return "public/booking-lookup";
        }

        Optional<Booking> result =
                bookingService.findBookingByCodeAndEmail(
                        bookingCode,
                        email
                );

        if (result.isEmpty()) {
            model.addAttribute(
                    "errorMessage",
                    "Booking tidak ditemukan. Periksa kembali kode booking dan email."
            );

            return "public/booking-lookup";
        }

        model.addAttribute("booking", result.get());

        return "public/booking-lookup-result";
    }
}
