package com.meetingroom.booking.config;

import com.meetingroom.booking.security.CustomAdminDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {

    private final CustomAdminDetailsService adminDetailsService;

    public SecurityConfig(
            CustomAdminDetailsService adminDetailsService) {
        this.adminDetailsService = adminDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .userDetailsService(adminDetailsService)

            .authorizeHttpRequests(auth -> auth

            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
        
            .requestMatchers(
                "/",
                "/booking",
                "/booking/success/**",
                "/booking/lookup",
                "/admin/login",
                "/error",
                "/css/**",
                "/js/**",
                "/images/**",
                "/favicon.ico"
            ).permitAll()
        
            .requestMatchers("/admin/**").hasRole("ADMIN")
        
            .anyRequest().authenticated()
        )

            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}