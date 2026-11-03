package com.meetingroom.booking.controller;

import com.meetingroom.booking.dto.RoomRequest;
import com.meetingroom.booking.entity.MeetingRoom;
import com.meetingroom.booking.service.MeetingRoomService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {

    private final MeetingRoomService meetingRoomService;

    public AdminRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    // Daftar ruangan
    @GetMapping
    public String index(Model model) {
        model.addAttribute("rooms", meetingRoomService.getAllRooms());
        return "admin/rooms/index";
    }

    // Form tambah ruangan
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("room", new RoomRequest());
        model.addAttribute("pageTitle", "Tambah Ruangan");

        return "admin/rooms/form";
    }

    // Form edit ruangan
    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {
        MeetingRoom existing = meetingRoomService.getRoomById(id);

        RoomRequest request = new RoomRequest();
        request.setRoomId(existing.getRoomId());
        request.setRoomName(existing.getRoomName());
        request.setLocation(existing.getLocation());
        request.setCapacity(existing.getCapacity());
        request.setFacilities(existing.getFacilities());
        request.setDescription(existing.getDescription());

        model.addAttribute("room", request);
        model.addAttribute("pageTitle", "Edit Ruangan");

        return "admin/rooms/form";
    }

    // Simpan ruangan
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("room") RoomRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "pageTitle",
                    request.getRoomId() == null
                            ? "Tambah Ruangan"
                            : "Edit Ruangan"
            );

            return "admin/rooms/form";
        }

        try {
            MeetingRoom room;

            if (request.getRoomId() != null) {
                room = meetingRoomService.getRoomById(request.getRoomId());
            } else {
                room = new MeetingRoom();
            }

            room.setRoomName(request.getRoomName().trim());
            room.setLocation(request.getLocation().trim());
            room.setCapacity(request.getCapacity());
            room.setFacilities(request.getFacilities());
            room.setDescription(request.getDescription());

            meetingRoomService.saveRoom(room);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Data ruangan berhasil disimpan."
            );

            return "redirect:/admin/rooms";

        } catch (IllegalArgumentException e) {
            model.addAttribute("pageTitle",
                    request.getRoomId() == null
                            ? "Tambah Ruangan"
                            : "Edit Ruangan");

            model.addAttribute("errorMessage", e.getMessage());

            return "admin/rooms/form";
        }
    }

    // Nonaktifkan ruangan
    @PostMapping("/{id}/deactivate")
    public String deactivate(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        meetingRoomService.deactivateRoom(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Ruangan berhasil dinonaktifkan."
        );

        return "redirect:/admin/rooms";
    }

    // Aktifkan ruangan
    @PostMapping("/{id}/activate")
    public String activate(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        meetingRoomService.activateRoom(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Ruangan berhasil diaktifkan."
        );

        return "redirect:/admin/rooms";
    }
}