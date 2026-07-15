package org.example.mappro.auth.repository;

import org.example.mappro.auth.model.Role;
import org.example.mappro.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Поиск пользователя по username (может понадобиться для авторизации)
    Optional<User> findByUsername(String username);

    // Поиск пользователя по email
    Optional<User> findByEmail(String email);

    // Проверка существования пользователя по username
    boolean existsByUsername(String username);

    // Проверка существования пользователя по email
    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
