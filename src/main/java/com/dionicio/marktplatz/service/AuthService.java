package com.dionicio.marktplatz.service;

import com.dionicio.marktplatz.dto.AuthResponse;
import com.dionicio.marktplatz.entity.RefreshToken;
import com.dionicio.marktplatz.entity.Role;
import com.dionicio.marktplatz.entity.User;
import com.dionicio.marktplatz.repository.RefreshTokenRepository;
import com.dionicio.marktplatz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(String email, String password) {
        String hash = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return issueTokens(user);
    }

    public AuthResponse login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return issueTokens(user);
    }

    public AuthResponse refreshAccessToken(String rawRefreshToken) {
        String userId = jwtService.extractUserId(rawRefreshToken);

        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid refresh token");
        }
        User user = userOptional.get();

        String incomingHash = hashToken(rawRefreshToken);
        List<RefreshToken> storedTokens = refreshTokenRepository.findByUser(user);

        boolean matchFound = storedTokens.stream()
                .anyMatch(stored -> stored.getTokenHash().equals(incomingHash));

        if (!matchFound) {
            throw new RuntimeException("Invalid refresh token");
        }

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateToken(user.getId().toString(), user.getRole().name());
        String rawRefreshToken = jwtService.generateRefreshToken(user.getId().toString());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawRefreshToken));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setCreatedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, rawRefreshToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}