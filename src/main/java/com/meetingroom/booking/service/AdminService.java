package com.meetingroom.booking.service;

import com.meetingroom.booking.entity.Admin;
import com.meetingroom.booking.repository.AdminRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Admin getAdminById(Long adminId) {

        return adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Admin tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public Admin getAdminByUsername(String username) {

        return adminRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Admin tidak ditemukan"));
    }

    public Admin saveAdmin(Admin admin) {

        if (admin.getUsername() == null
                || admin.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "Username wajib diisi");
        }

        if (admin.getPassword() == null
                || admin.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Password wajib diisi");
        }

        return adminRepository.save(admin);
    }

    public void deleteAdmin(Long adminId) {

        Admin admin = getAdminById(adminId);

        adminRepository.delete(admin);
    }
}