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

@Service
@Slf4j
@RequiredArgsConstructor
public class ViolationTypeService {

    private final ViolationTypeRepository violationTypeRepository;

    @Transactional
    public ViolationTypeResponseDto createViolationType(ViolationTypeCreateDto dto) {
        if (violationTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new ResourceConflictException(
                "VIOLATION_TYPE_NAME_CONFLICT",
                "ViolationType with type '" + dto.getName() + "' already exists"
            );
        }
        
        ViolationType violationType = new ViolationType();
        violationType.setName(dto.getName());

        ViolationType saved = violationTypeRepository.save(violationType);
        log.info("Created violation type with id: {}", saved.getId());

        return mapToResponseDto(saved);
    }

    private ViolationTypeResponseDto mapToResponseDto(ViolationType violationType) {
        return ViolationTypeResponseDto.builder()
            .id(violationType.getId())
            .name(violationType.getName())
            .build();
    }
}
