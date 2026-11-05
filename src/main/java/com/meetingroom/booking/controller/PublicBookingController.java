package com.meetingroom.booking.controller;

import com.meetingroom.booking.dto.BookingRequest;
import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.mapper.BookingMapper;
import com.meetingroom.booking.service.BookingService;
import com.meetingroom.booking.service.MeetingRoomService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PublicBookingController {

    private final MeetingRoomService meetingRoomService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public PublicBookingController(
            MeetingRoomService meetingRoomService,
            BookingService bookingService,
            BookingMapper bookingMapper) {

        this.meetingRoomService = meetingRoomService;
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    // ==========================================
    // HALAMAN BOOKING
    // ==========================================

    @GetMapping("/booking")
    public String showBookingPage(Model model) {

        model.addAttribute(
                "rooms",
                meetingRoomService.getActiveRooms()
        );

        model.addAttribute(
                "bookingRequest",
                new BookingRequest()
        );

        return "public/booking";
    }

    // ==========================================
    // PROSES BOOKING
    // ==========================================

    @PostMapping("/booking")
    public String createBooking(
            @Valid @ModelAttribute("bookingRequest")
            BookingRequest bookingRequest,

            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "rooms",
                    meetingRoomService.getActiveRooms()
            );

            return "public/booking";
        }

        try {

            Booking booking = bookingMapper.toEntity(bookingRequest);

            Booking savedBooking =
                    bookingService.createBooking(booking);

            return "redirect:/booking/success/"
                    + savedBooking.getBookingCode();

        } catch (IllegalArgumentException
                | IllegalStateException exception) {

            model.addAttribute(
                    "rooms",
                    meetingRoomService.getActiveRooms()
            );

            model.addAttribute(
                    "bookingError",
                    exception.getMessage()
            );

            return "public/booking";
        }
    }

    // ==========================================
    // HALAMAN BOOKING BERHASIL
    // ==========================================

    @GetMapping("/booking/success/{bookingCode}")
    public String bookingSuccess(
            @PathVariable String bookingCode,
            Model model) {

        try {

            Booking booking =
                    bookingService.getBookingByCode(bookingCode);

            model.addAttribute("booking", booking);

            return "public/booking-success";

        } catch (IllegalArgumentException exception) {

            return "redirect:/";
        }
    }
}