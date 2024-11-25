package com.example.flappybird.controller;

import com.example.flappybird.model.User;
import com.example.flappybird.repository.UserRepository;
import com.example.flappybird.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Xử lý yêu cầu quên mật khẩu, tạo mã xác minh và gửi qua email.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByUsername(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Tạo mã xác minh (6 chữ số)
        String resetToken = generateVerificationCode();
        user.setResetToken(resetToken);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Token hết hạn sau 15 phút
        userRepository.save(user);

        // Gửi email
        sendVerificationEmail(email, resetToken);

        return ResponseEntity.ok("Verification token sent to your email.");
    }

    /**
     * Xác minh mã xác minh (token).
     */
    @PostMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String email, @RequestParam String token) {
        User user = userRepository.findByUsername(email);
        if (user == null || !token.equals(user.getResetToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
        }

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired.");
        }

        return ResponseEntity.ok("Token is valid.");
    }

    /**
     * Đặt lại mật khẩu sau khi xác minh thành công.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        User user = userRepository.findByUsername(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user.");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Xóa token sau khi sử dụng
        user.setTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully.");
    }

    /**
     * Tạo mã xác minh (6 chữ số).
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * Gửi email xác minh chứa mã xác minh.
     */
    private void sendVerificationEmail(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Verification Code");
        message.setText("Your password reset verification code is: " + resetToken + "\n" +
                "This code is valid for 15 minutes.");
        mailSender.send(message);
    }
}
