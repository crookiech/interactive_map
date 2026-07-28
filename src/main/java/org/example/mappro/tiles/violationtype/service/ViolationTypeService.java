package org.example.mappro.tiles.violationtype.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.mappro.tiles.violationtype.dto.ViolationTypeCreateDto;
import org.example.mappro.tiles.violationtype.dto.ViolationTypeResponseDto;
import org.example.mappro.tiles.violationtype.model.ViolationType;
import org.example.mappro.tiles.violationtype.repository.ViolationTypeRepository;
import org.example.mappro.exception.ResourceConflictException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViolationTypeService {

    private final ViolationTypeRepository violationTypeRepository;

    @Transactional
    public ViolationTypeResponseDto createViolationType(ViolationTypeCreateDto dto) {
        String code = dto.getCode().trim().toUpperCase(java.util.Locale.ROOT);
        if (violationTypeRepository.findByCode(code).isPresent()) {
            throw new ResourceConflictException(
                "VIOLATION_TYPE_CODE_CONFLICT",
                "Violation type with code '" + code + "' already exists"
            );
        }
        
        ViolationType violationType = new ViolationType();
        violationType.setCode(code);
        violationType.setDisplayName(dto.getDisplayName().trim());

        ViolationType saved = violationTypeRepository.save(violationType);
        log.info("Created violation type with id: {}", saved.getId());

        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ViolationTypeResponseDto> getViolationTypes() {
        return violationTypeRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private ViolationTypeResponseDto mapToResponseDto(ViolationType violationType) {
        return ViolationTypeResponseDto.builder()
            .id(violationType.getId())
            .code(violationType.getCode())
            .displayName(violationType.getDisplayName())
            .build();
    }
}
