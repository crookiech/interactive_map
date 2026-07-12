package org.example.mappro.tiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.dto.ViolationCreateDto;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.Violation;
import org.example.mappro.tiles.repository.GeoRepository;
import org.example.mappro.tiles.repository.ViolationRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final GeoRepository geoRepository;
    private final ViolationRepository violationRepository;
    private final WKTReader wktReader = new WKTReader();

    @Transactional
    public GeoObject createGeoObject(GeoObjectCreateDto dto) {
        try {
            Geometry geometry = wktReader.read(dto.getGeometryWkt());
            // Устанавливаем SRID=4326 (градусы)
            geometry.setSRID(4326);

            GeoObject geoObject = new GeoObject();
            geoObject.setName(dto.getName());
            geoObject.setType(dto.getType());
            geoObject.setGeometry(geometry);
            geoObject.setParentId(dto.getParentId());
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
        if (!geoRepository.existsById(dto.getGeoObjectId())) {
            throw new RuntimeException("GeoObject with id " + dto.getGeoObjectId() + " not found");
        }
        Violation violation = new Violation();
        violation.setGeoObjectId(dto.getGeoObjectId());
        violation.setType(dto.getType());
        violation.setSeverity(dto.getSeverity());
        violation.setDate(dto.getDate());
        violation.setDescription(dto.getDescription());
        return violationRepository.save(violation);
    }
}