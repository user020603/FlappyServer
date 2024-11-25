package com.example.flappybird.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 6)
    private String resetToken; // Mã xác minh (6 chữ số)

    private LocalDateTime tokenExpiry; // Thời gian hết hạn của mã xác minh
}
