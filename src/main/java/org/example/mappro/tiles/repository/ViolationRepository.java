package org.example.mappro.tiles.repository;

import org.example.mappro.tiles.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
    List<Violation> findBySeverity(String severity);

    List<Violation> findByStatus(String status);

    List<Violation> findByDate(LocalDateTime date);

    List<Violation> findByType(Long type);
}