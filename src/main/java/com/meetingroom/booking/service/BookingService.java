package com.meetingroom.booking.service;

import com.meetingroom.booking.entity.Booking;
import com.meetingroom.booking.entity.MeetingRoom;
import com.meetingroom.booking.repository.BookingRepository;
import com.meetingroom.booking.repository.MeetingRoomRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.util.Optional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MeetingRoomRepository meetingRoomRepository;

    public BookingService(
            BookingRepository bookingRepository,
            MeetingRoomRepository meetingRoomRepository) {

        this.bookingRepository = bookingRepository;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    // ==========================================
    // BOOKING PUBLIC
    // ==========================================

    public Booking createBooking(Booking booking) {

        // Validasi waktu
        if (booking.getStartTime() == null
                || booking.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Waktu booking wajib diisi");
        }

        if (!booking.getEndTime()
                .isAfter(booking.getStartTime())) {

            throw new IllegalArgumentException(
                    "Waktu selesai harus setelah waktu mulai");
        }

        if (booking.getStartTime()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Booking tidak boleh untuk waktu yang sudah lewat");
        }

        // Validasi ruangan
        if (booking.getMeetingRoom() == null
                || booking.getMeetingRoom().getRoomId() == null) {

            throw new IllegalArgumentException(
                    "Ruangan wajib dipilih");
        }

        Long roomId = booking.getMeetingRoom().getRoomId();

        // Lock ruangan untuk mencegah booking bersamaan
        MeetingRoom room = meetingRoomRepository
                .findByIdForUpdate(roomId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ruangan tidak ditemukan"));

        // Pastikan ruangan aktif
        if (!"ACTIVE".equals(room.getStatus())) {

            throw new IllegalStateException(
                    "Ruangan sedang tidak tersedia");
        }

        // Cek jadwal bentrok
        long conflict = bookingRepository
                .countConflictingBookings(
                        roomId,
                        booking.getStartTime(),
                        booking.getEndTime());

        if (conflict > 0) {

            throw new IllegalStateException(
                    "Jadwal booking bentrok dengan booking lain");
        }

        // Generate booking code
        booking.setBookingCode(generateBookingCode());

        // Hubungkan booking dengan ruangan yang valid
        booking.setMeetingRoom(room);

        // Booking langsung confirmed
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    // ==========================================
    // GENERATE BOOKING CODE
    // ==========================================

    private String generateBookingCode() {

        return "MRB-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    // ==========================================
    // ADMIN BOOKING MANAGEMENT
    // ==========================================

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public Booking getBookingByCode(String bookingCode) {

        return bookingRepository
                .findByBookingCode(bookingCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByRoom(Long roomId) {

        return bookingRepository
                .findByMeetingRoomRoomIdOrderByStartTimeAsc(roomId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByDate(
            LocalDateTime dayStart,
            LocalDateTime dayEnd) {

        return bookingRepository
                .findBookingsByDate(dayStart, dayEnd);
    }

    // ==========================================
    // CANCEL BOOKING
    // ==========================================

    public Booking cancelBooking(Long bookingId) {

        Booking booking = getBookingById(bookingId);

        if (!"CONFIRMED".equals(booking.getStatus())) {

            throw new IllegalStateException(
                    "Booking tidak dapat dibatalkan");
        }

        booking.setStatus("CANCELLED");

        return bookingRepository.save(booking);
    }
    
    @Transactional(readOnly = true)
    public long countAllBookings() {
        return bookingRepository.count();
    }
    
    @Transactional(readOnly = true)
    public long countConfirmedBookings() {
        return bookingRepository.countByStatus("CONFIRMED");
    }
    
    @Transactional(readOnly = true)
    public long countCancelledBookings() {
        return bookingRepository.countByStatus("CANCELLED");
    }
    
    @Transactional(readOnly = true)
    public List<Booking> getRecentBookings() {
        return bookingRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    // @Transactional(readOnly = true)
    // public List<Booking> getAllBookings() {
    //     return bookingRepository.findAll(
    //             org.springframework.data.domain.Sort
    //                     .by(org.springframework.data.domain.Sort.Direction.DESC, "startTime")
    //     );
    // }
    
    @Transactional
    public void completeBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
    
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalArgumentException(
                    "Booking hanya bisa diselesaikan jika status CONFIRMED."
            );
        }
    
        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);
    }
    
    @Transactional
    public void cancelBookingByAdmin(Long bookingId) {
        Booking booking = getBookingById(bookingId);
    
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalArgumentException(
                    "Booking ini tidak dapat dibatalkan."
            );
        }
    
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
    
    // @Transactional(readOnly = true)
    // public Booking getBookingById(Long bookingId) {
    //     return bookingRepository.findById(bookingId)
    //             .orElseThrow(() -> new IllegalArgumentException(
    //                     "Booking tidak ditemukan dengan ID: " + bookingId
    //             ));
    // }
    
    @Transactional(readOnly = true)
    public Optional<Booking> findBookingByCodeAndEmail(
            String bookingCode,
            String email
    ) {
        return bookingRepository.findByBookingCodeAndEmailIgnoreCase(
                bookingCode.trim(),
                email.trim()
        );
    }
    
    @Transactional(readOnly = true)
    public List<Booking> getTodayBookings() {
        LocalDate today = LocalDate.now();
    
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
    
        return bookingRepository.findBookingsByDate(dayStart, dayEnd)
            .stream()
            .filter(booking -> !"CANCELLED".equals(booking.getStatus()))
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByWeek(
            LocalDateTime weekStart,
            LocalDateTime weekEnd) {
    
        return bookingRepository.findBookingsByWeek(
                weekStart,
                weekEnd
        );
    }
}