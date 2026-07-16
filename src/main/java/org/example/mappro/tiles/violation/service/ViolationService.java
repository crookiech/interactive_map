package org.example.mappro.tiles.violation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.violation.dto.ViolationResponseDto;
import org.example.mappro.tiles.violation.dto.ViolationCreateDto;
import org.example.mappro.tiles.violation.model.Violation;
import org.example.mappro.tiles.violationtype.model.ViolationType;
import org.example.mappro.tiles.violation.repository.ViolationRepository;
import org.example.mappro.tiles.violationtype.repository.ViolationTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViolationService {
    
    private final ViolationRepository violationRepository;
    private final ViolationTypeRepository violationTypeRepository;

    @Transactional
    public ViolationResponseDto createViolation(ViolationCreateDto dto) {
        ViolationType type = violationTypeRepository.findByName(dto.getType())
                .orElseThrow(() -> new RuntimeException("Violation type not found: " + dto.getType()));
        
        Violation violation = new Violation();
        violation.setType(type);
        violation.setSeverity(dto.getSeverity());
        violation.setDate(dto.getDate() != null ? dto.getDate() : LocalDateTime.now());
        violation.setDescription(dto.getDescription());
        violation.setStatus("ACTIVE"); // Установите статус по умолчанию
        
        Violation saved = violationRepository.save(violation);
        log.info("Created violation with id: {}", saved.getId());
        
        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public ViolationResponseDto getViolation(Long id) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Violation not found with id: " + id));
        return mapToResponseDto(violation);
    }

    @Transactional(readOnly = true)
    public List<ViolationResponseDto> getAllViolations() {
        return violationRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ViolationResponseDto> getBySeverity(String severity) {
        return violationRepository.findBySeverity(severity).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ViolationResponseDto mapToResponseDto(Violation violation) {
        return ViolationResponseDto.builder()
                .id(violation.getId())
                .type(violation.getType() != null ? violation.getType().getName() : null)
                .severity(violation.getSeverity())
                .date(violation.getDate())
                .description(violation.getDescription())
                .status(violation.getStatus())
                .build();
    }
}