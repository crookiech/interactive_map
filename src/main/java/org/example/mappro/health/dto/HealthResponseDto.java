package org.example.mappro.health.dto;

import java.time.Instant;

public record HealthResponseDto(
        String status,
        String service,
        Instant timestamp
) {
}
