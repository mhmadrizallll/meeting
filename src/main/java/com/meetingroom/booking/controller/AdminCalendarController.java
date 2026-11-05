
package com.meetingroom.booking.controller;

import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.service.BookingService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/calendar")
public class AdminCalendarController {

    private final BookingService bookingService;

    public AdminCalendarController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String calendar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Model model) {

        // Jika tanggal tidak dipilih, gunakan hari ini
        LocalDate selectedDate = date != null
                ? date
                : LocalDate.now();

        // Awal minggu adalah hari Senin
        LocalDate weekStart = selectedDate.with(
                DayOfWeek.MONDAY
        );

        // Akhir minggu adalah Senin berikutnya
        LocalDate weekEnd = weekStart.plusWeeks(1);

        LocalDateTime startDateTime = weekStart.atStartOfDay();
        LocalDateTime endDateTime = weekEnd.atStartOfDay();

        // Ambil seluruh booking pada minggu tersebut
        List<Booking> bookings = bookingService.getBookingsByWeek(
                startDateTime,
                endDateTime
        );

        // Siapkan daftar tujuh hari
        List<LocalDate> weekDays = new ArrayList<>();

        Map<LocalDate, List<Booking>> bookingsByDay =
                new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {

            LocalDate day = weekStart.plusDays(i);

            weekDays.add(day);
            bookingsByDay.put(day, new ArrayList<>());
        }

        // Masukkan booking ke setiap hari yang bersinggungan
        for (Booking booking : bookings) {

            for (LocalDate day : weekDays) {

                LocalDateTime dayStart = day.atStartOfDay();
                LocalDateTime dayEnd = day.plusDays(1).atStartOfDay();

                boolean overlaps =
                        booking.getStartTime().isBefore(dayEnd)
                        && booking.getEndTime().isAfter(dayStart);

                if (overlaps) {
                    bookingsByDay.get(day).add(booking);
                }
            }
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy");

        model.addAttribute("weekDays", weekDays);
        model.addAttribute("bookingsByDay", bookingsByDay);
        model.addAttribute("bookings", bookings);

        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd.minusDays(1));

        model.addAttribute(
                "weekLabel",
                weekStart.format(formatter)
                        + " - "
                        + weekEnd.minusDays(1).format(formatter)
        );

        // Navigasi minggu sebelumnya dan berikutnya
        model.addAttribute(
                "previousWeek",
                weekStart.minusWeeks(1)
        );

        model.addAttribute(
                "nextWeek",
                weekStart.plusWeeks(1)
        );

        model.addAttribute("selectedDate", selectedDate);

        return "admin/calendar";
    }
}
