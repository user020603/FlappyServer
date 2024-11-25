package com.example.flappybird.config;

import com.example.flappybird.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF sử dụng cú pháp mới trong Spring Security 6.1+
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/register", "/api/login", "/api/forgot-password", "api/reset-password", "api/verify-token").permitAll() // Cho phép truy cập không cần xác thực
                        .anyRequest().authenticated() // Yêu cầu xác thực cho các endpoint khác
                ).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Sử dụng JWT;

        // Thêm JwtFilter vào chuỗi lọc
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

