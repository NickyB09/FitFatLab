package com.fitfatlab.fitfatlab_backend.modules.auth.repository;

import com.fitfatlab.fitfatlab_backend.modules.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
