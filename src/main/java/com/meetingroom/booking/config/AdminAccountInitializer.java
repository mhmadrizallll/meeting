package com.meetingroom.booking.config;

import com.meetingroom.booking.entity.Admin;
import com.meetingroom.booking.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial-username:}")
    private String username;

    @Value("${app.admin.initial-password:}")
    private String password;

    @Value("${app.admin.initial-email:}")
    private String email;

    @Value("${app.admin.initial-full-name:}")
    private String fullName;

    public AdminAccountInitializer(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        // Tidak membuat akun jika konfigurasi belum diberikan
        if (username.isBlank()
                && password.isBlank()
                && email.isBlank()) {
            return;
        }

        if (username.isBlank()
                || password.isBlank()
                || email.isBlank()
                || fullName.isBlank()) {

            throw new IllegalStateException(
                    "Konfigurasi akun admin awal belum lengkap"
            );
        }

        if (adminRepository.existsByUsername(username)) {
            System.out.println("Akun admin sudah tersedia.");
            return;
        }

        if (adminRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException(
                    "Email admin sudah digunakan."
            );
        }

        Admin admin = new Admin();

        admin.setFullName(fullName);
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));

        adminRepository.save(admin);

        System.out.println("Akun admin awal berhasil dibuat.");
    }
}