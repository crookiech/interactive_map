package org.example.mappro.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.mappro.auth.model.RefreshTokenEntity;
import org.example.mappro.auth.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public boolean isValid(String username, String token) {
        return repository.findByUsernameAndToken(username, token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void rotate(String username, String oldToken, String newToken, Duration ttl) {
        // Удаляем старый токен
        repository.deleteByUsernameAndToken(username, oldToken);

        // Сохраняем новый
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUsername(username);
        entity.setToken(newToken);
        entity.setExpiresAt(LocalDateTime.now().plus(ttl));
        entity.setRevoked(false);
        repository.save(entity);
    }

    @Transactional
    public void removeByUsernameAndToken(String username, String token) {
        repository.deleteByUsernameAndToken(username, token);
    }

    @Transactional
    public void save(String username, String token, Duration ttl) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUsername(username);
        entity.setToken(token);
        entity.setExpiresAt(LocalDateTime.now().plus(ttl));
        entity.setRevoked(false);
        repository.save(entity);
    }

    @Transactional
    public void removeAllByUsername(String username) {
        repository.deleteByUsername(username);
    }
}