package com.meetingroom.booking;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConnectionCheck {

    @Bean
    ApplicationRunner checkOracleConnection(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("======================================");
                System.out.println("ORACLE CONNECTION SUCCESS");
                System.out.println(
                    "Database: "
                    + connection.getMetaData().getDatabaseProductName()
                );
                System.out.println(
                    "Version: "
                    + connection.getMetaData().getDatabaseProductVersion()
                );
                System.out.println("======================================");
            } catch (SQLException e) {
                System.err.println("ORACLE CONNECTION FAILED");
                System.err.println(e.getMessage());
                System.err.println(
                    "Periksa service name, username, password, "
                    + "dan status Oracle."
                );
            }
        };
    }
}
