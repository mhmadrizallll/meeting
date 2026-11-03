package com.meetingroom.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;

public class BookingRequest {

    @NotNull(message = "Ruangan wajib dipilih")
    private Long roomId;

    @NotBlank(message = "Nama pemesan wajib diisi")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String bookerName;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 150, message = "Email maksimal 150 karakter")
    private String email;

    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(
        regexp = "^[0-9+\\- ]{8,20}$",
        message = "Nomor telepon harus 8–20 karakter angka"
    )
    private String phone;

    @Size(max = 100, message = "Departemen maksimal 100 karakter")
    private String department;

    @NotBlank(message = "Agenda meeting wajib diisi")
    @Size(max = 500, message = "Agenda maksimal 500 karakter")
    private String agenda;

    @NotNull(message = "Waktu mulai wajib diisi")
    @Future(message = "Waktu mulai harus di masa depan")
    private LocalDateTime startTime;

    @NotNull(message = "Waktu selesai wajib diisi")
    @Future(message = "Waktu selesai harus di masa depan")
    private LocalDateTime endTime;

    // Getter dan Setter

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getBookerName() {
        return bookerName;
    }

    public void setBookerName(String bookerName) {
        this.bookerName = bookerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    @AssertTrue(message = "Waktu selesai harus setelah waktu mulai")
    public boolean isEndTimeAfterStartTime() {
    
        if (startTime == null || endTime == null) {
            return true;
        }
    
        return endTime.isAfter(startTime);
    }
}