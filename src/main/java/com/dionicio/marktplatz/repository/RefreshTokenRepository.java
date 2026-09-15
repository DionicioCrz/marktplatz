package com.dionicio.marktplatz.repository;

import com.dionicio.marktplatz.entity.RefreshToken;
import com.dionicio.marktplatz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUser(User user);
}