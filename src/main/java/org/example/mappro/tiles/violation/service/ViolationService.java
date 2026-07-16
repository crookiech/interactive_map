// package org.example.mappro.tiles.violation.service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.example.mappro.tiles.violation.dto.ViolationCreateDto;
// import org.example.mappro.tiles.violation.model.Violation;
// import org.example.mappro.tiles.violationtype.model.ViolationType;
// import org.example.mappro.tiles.violation.repository.ViolationRepository;
// import org.example.mappro.tiles.violationtype.repository.ViolationTypeRepository;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @Slf4j
// @RequiredArgsConstructor
// public class ViolationService {
    
//     private final ViolationRepository violationRepository;
//     private final ViolationTypeRepository violationTypeRepository;

//     @Transactional
//     public Violation createViolation(ViolationCreateDto dto) {
//         ViolationType type = violationTypeRepository.findByName(dto.getType()).orElseThrow(() -> new RuntimeException("Violation type not found: " + dto.getType()));
//         Violation violation = new Violation();
//         violation.setType(type);
//         violation.setSeverity(dto.getSeverity());
//         violation.setDate(dto.getDate());
//         violation.setDescription(dto.getDescription());
//         return violationRepository.save(violation);
//     }
// }
