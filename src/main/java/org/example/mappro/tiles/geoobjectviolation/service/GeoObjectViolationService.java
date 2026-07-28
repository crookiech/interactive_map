package org.example.mappro.tiles.geoobjectviolation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.geoobject.model.GeoObject;
import org.example.mappro.tiles.geoobject.repository.GeoObjectRepository;
import org.example.mappro.tiles.geoobjectviolation.dto.GeoObjectViolationCreateDto;
import org.example.mappro.tiles.geoobjectviolation.dto.GeoObjectViolationResponseDto;
import org.example.mappro.tiles.geoobjectviolation.model.GeoObjectViolation;
import org.example.mappro.tiles.geoobjectviolation.repository.GeoObjectViolationRepository;
import org.example.mappro.tiles.violation.model.Violation;
import org.example.mappro.tiles.violation.repository.ViolationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.example.mappro.exception.ResourceConflictException;
import org.example.mappro.exception.ResourceNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectViolationService {
    private final GeoObjectViolationRepository geoObjectViolationRepository;
    private final GeoObjectRepository geoObjectRepository;
    private final ViolationRepository violationRepository;

    @Transactional
    public GeoObjectViolationResponseDto createGeoObjectViolation(GeoObjectViolationCreateDto dto) {
        if (geoObjectViolationRepository.existsByGeoObjectIdAndViolationId(dto.getObjectId(), dto.getViolationId())) {
            throw new ResourceConflictException(
                "GEO_OBJECT_VIOLATION_CONFLICT",
                "Relation already exists between object " + dto.getObjectId() + " and violation " + dto.getViolationId()
            );
        }

        GeoObject geoObject = geoObjectRepository.findById(dto.getObjectId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "GEO_OBJECT_NOT_FOUND",
                "GeoObject not found: " + dto.getObjectId()
            ));

        Violation violation = violationRepository.findById(dto.getViolationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "VIOLATION_NOT_FOUND",
                "Violation not found: " + dto.getViolationId()
            ));

        GeoObjectViolation geoObjectViolation = new GeoObjectViolation();
        geoObjectViolation.setGeoObject(geoObject);
        geoObjectViolation.setViolation(violation);

        GeoObjectViolation saved = geoObjectViolationRepository.save(geoObjectViolation);
        log.info("Created GeoObjectViolation with id: {} for object {} and violation {}", saved.getId(), dto.getObjectId(), dto.getViolationId());
        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public GeoObjectViolationResponseDto getGeoObjectViolation(Long id) {
        GeoObjectViolation geoObjectViolation = geoObjectViolationRepository.findById(id)
            .orElseThrow(() -> relationNotFound(id));
        return mapToResponseDto(geoObjectViolation);
    }

    @Transactional(readOnly = true)
    public List<GeoObjectViolationResponseDto> getAllGeoObjectViolations() {
        return geoObjectViolationRepository.findAll().stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GeoObjectViolationResponseDto> getViolationsByObjectId(Long objectId) {
        return geoObjectViolationRepository.findViolationsByObjectId(objectId).stream()
            .map(violation -> {
                GeoObjectViolation gov = geoObjectViolationRepository.findAll().stream()
                    .filter(g -> g.getGeoObject().getId().equals(objectId) && g.getViolation().getId().equals(violation.getId()))
                    .findFirst()
                    .orElse(null);
                return gov != null ? mapToResponseDto(gov) : null;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GeoObjectViolationResponseDto> getObjectsByViolationId(Long violationId) {
        return geoObjectViolationRepository.findObjectsByViolationId(violationId).stream()
            .map(geoObject -> {
                GeoObjectViolation gov = geoObjectViolationRepository.findAll().stream()
                        .filter(g -> g.getGeoObject().getId().equals(geoObject.getId()) && g.getViolation().getId().equals(violationId))
                        .findFirst()
                        .orElse(null);
                return gov != null ? mapToResponseDto(gov) : null;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGeoObjectViolation(Long id) {
        if (!geoObjectViolationRepository.existsById(id)) {
            throw relationNotFound(id);
        }
        geoObjectViolationRepository.deleteById(id);
        log.info("Deleted GeoObjectViolation with id: {}", id);
    }

    @Transactional
    public void deleteGeoObjectViolationByObjectAndViolation(Long objectId, Long violationId) {
        if (!geoObjectViolationRepository.existsByGeoObjectIdAndViolationId(objectId, violationId)) {
            throw new ResourceNotFoundException(
                "GEO_OBJECT_VIOLATION_NOT_FOUND",
                "Relation not found between object " + objectId + " and violation " + violationId
            );
        }
        geoObjectViolationRepository.deleteByGeoObjectIdAndViolationId(objectId, violationId);
        log.info("Deleted relation between object {} and violation {}", objectId, violationId);
    }

    @Transactional(readOnly = true)
    public boolean existsRelation(Long objectId, Long violationId) {
        return geoObjectViolationRepository.existsByGeoObjectIdAndViolationId(objectId, violationId);
    }

    private ResourceNotFoundException relationNotFound(Long id) {
        return new ResourceNotFoundException(
            "GEO_OBJECT_VIOLATION_NOT_FOUND",
            "GeoObjectViolation not found with id: " + id
        );
    }

    private GeoObjectViolationResponseDto mapToResponseDto(GeoObjectViolation geoObjectViolation) {
        GeoObject geoObject = geoObjectViolation.getGeoObject();
        Violation violation = geoObjectViolation.getViolation();

        return GeoObjectViolationResponseDto.builder()
            .id(geoObjectViolation.getId())
            .objectId(geoObject != null ? geoObject.getId() : null)
            .objectName(geoObject != null ? geoObject.getName() : null)
            .objectType(geoObject != null && geoObject.getType() != null ? geoObject.getType().getCode() : null)
            .violationId(violation != null ? violation.getId() : null)
            .violationTypeCode(violation != null && violation.getType() != null ? violation.getType().getCode() : null)
            .violationTypeName(violation != null && violation.getType() != null ? violation.getType().getDisplayName() : null)
            .violationSeverity(violation != null ? violation.getSeverity() : null)
            .violationDescription(violation != null ? violation.getDescription() : null)
            .status(violation != null ? violation.getStatus() : null)
            .build();
    }
}
