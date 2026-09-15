package com.dionicio.marktplatz.controller;

import com.dionicio.marktplatz.dto.AuthRequest;
import com.dionicio.marktplatz.dto.AuthResponse;
import com.dionicio.marktplatz.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/v1/auth/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return authService.register(request.email(), request.password());
    }

    @PostMapping("/api/v1/auth/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request.email(), request.password());
    }

    @PostMapping("/api/v1/auth/refresh")
    public AuthResponse refresh(@RequestBody String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }

    @GetMapping("/api/v1/auth/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "You are an admin!";
    }
}
