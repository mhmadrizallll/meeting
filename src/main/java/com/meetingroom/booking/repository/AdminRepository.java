
package com.meetingroom.booking.repository;

import com.meetingroom.booking.entity.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository
        extends JpaRepository<Admin, Long> {

    // Mencari admin berdasarkan username
    Optional<Admin> findByUsername(String username);

    // Mencari admin berdasarkan email
    Optional<Admin> findByEmailIgnoreCase(String email);

    // Memeriksa username sudah digunakan
    boolean existsByUsername(String username);

    // Memeriksa email sudah digunakan
    boolean existsByEmailIgnoreCase(String email);
}
