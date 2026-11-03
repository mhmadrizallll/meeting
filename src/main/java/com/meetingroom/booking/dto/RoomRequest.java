package com.meetingroom.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RoomRequest {

    private Long roomId;

    @NotBlank(message = "Nama ruangan wajib diisi")
    @Size(max = 100, message = "Nama ruangan maksimal 100 karakter")
    private String roomName;

    @NotBlank(message = "Lokasi wajib diisi")
    @Size(max = 150, message = "Lokasi maksimal 150 karakter")
    private String location;

    @NotNull(message = "Kapasitas wajib diisi")
    @Min(value = 1, message = "Kapasitas minimal 1 orang")
    private Integer capacity;

    @Size(max = 1000, message = "Fasilitas maksimal 1000 karakter")
    private String facilities;

    @Size(max = 1000, message = "Deskripsi maksimal 1000 karakter")
    private String description;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}