package org.example.mappro.tiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.dto.ViolationCreateDto;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.GeoObjectType;
import org.example.mappro.tiles.model.Violation;
import org.example.mappro.tiles.model.ViolationType;
import org.example.mappro.tiles.repository.GeoObjectRepository;
import org.example.mappro.tiles.repository.GeoObjectTypeRepository;
import org.example.mappro.tiles.repository.ViolationRepository;
import org.example.mappro.tiles.repository.ViolationTypeRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final GeoObjectRepository geoRepository;
    private final ViolationRepository violationRepository;
    private final GeoObjectTypeRepository geoObjectTypeRepository;
    private final ViolationTypeRepository violationTypeRepository;
    private final WKTReader wktReader = new WKTReader();

    @Transactional
    public GeoObject createGeoObject(GeoObjectCreateDto dto) {
        try {
            GeoObjectType type = geoObjectTypeRepository.findByName(dto.getType()).orElseThrow(() -> new RuntimeException("Object type not found: " + dto.getType()));

            Geometry geometry = wktReader.read(dto.getGeometryWkt());
            geometry.setSRID(4326);

            GeoObject geoObject = new GeoObject();
            geoObject.setName(dto.getName());
            geoObject.setType(type);
            geoObject.setGeometry(geometry);
            
            if (dto.getParentId() != null) {
                GeoObject parent = geoRepository.findById(dto.getParentId()).orElseThrow(() -> new RuntimeException("Parent not found: " + dto.getParentId()));
                geoObject.setParent(parent);
            }
            
            geoObject.setLodMin(dto.getLodMin());
            geoObject.setLodMax(dto.getLodMax());
            geoObject.setLabelPriority(dto.getLabelPriority());
            geoObject.setIsSegment(dto.getIsSegment());
            geoObject.setSegmentOrder(dto.getSegmentOrder());

            return geoRepository.save(geoObject);
            
        } catch (ParseException e) {
            throw new RuntimeException("Invalid WKT geometry: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Violation createViolation(ViolationCreateDto dto) {
        ViolationType type = violationTypeRepository.findByName(dto.getType()).orElseThrow(() -> new RuntimeException("Violation type not found: " + dto.getType()));
        Violation violation = new Violation();
        violation.setType(type);
        violation.setSeverity(dto.getSeverity());
        violation.setDate(dto.getDate());
        violation.setDescription(dto.getDescription());
        return violationRepository.save(violation);
    }

    @Transactional
    public GeoObjectType createGeoObjectType(GeoObjectType dto) {
        if (geoObjectTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("GeoObjectType with type '" + dto.getName() + "' already exists");
        }
        
        GeoObjectType geoObjectType = new GeoObjectType();
        geoObjectType.setName(dto.getName());
        
        return geoObjectTypeRepository.save(geoObjectType);
    }

    @Transactional
    public ViolationType createViolationType(ViolationType dto) {
        if (violationTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("GeoObjectType with type '" + dto.getName() + "' already exists");
        }
        
        ViolationType violationType = new ViolationType();
        violationType.setName(dto.getName());
        
        return violationTypeRepository.save(violationType);
    }
}
