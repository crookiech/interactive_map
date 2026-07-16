package org.example.mappro.auth.repository;

import org.example.mappro.auth.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUsernameAndToken(String username, String token);
    void deleteByUsernameAndToken(String username, String token);
    void deleteByUsername(String username);
}
