package org.example.mappro.tiles.violationtype.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.mappro.tiles.violationtype.model.ViolationType;
import org.example.mappro.tiles.violationtype.repository.ViolationTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViolationTypeService {

    private final ViolationTypeRepository violationTypeRepository;

    @Transactional
    public ViolationType createViolationType(ViolationType dto) {
        if (violationTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("ViolationType with type '" + dto.getName() + "' already exists");
        }
        
        ViolationType violationType = new ViolationType();
        violationType.setName(dto.getName());

        return violationTypeRepository.save(violationType);
    }
}
