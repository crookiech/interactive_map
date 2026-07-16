package org.example.mappro.tiles.geoobject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.mappro.tiles.geoobject.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.geoobject.dto.GeoObjectResponseDto;
import org.example.mappro.tiles.geoobject.model.GeoObject;
import org.example.mappro.tiles.geoobject.repository.GeoObjectRepository;
import org.example.mappro.tiles.geoobjecttype.model.GeoObjectType;
import org.example.mappro.tiles.geoobjecttype.repository.GeoObjectTypeRepository;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.geom.Geometry;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectService {

    private final GeoObjectRepository geoObjectRepository;
    private final GeoObjectTypeRepository geoObjectTypeRepository;
    private final WKTReader wktReader = new WKTReader();

    @Transactional
    public GeoObjectResponseDto createGeoObject(GeoObjectCreateDto dto) {
        try {
            GeoObjectType type = geoObjectTypeRepository.findByName(dto.getType())
                    .orElseThrow(() -> new RuntimeException("Object type not found: " + dto.getType()));

            Geometry geometry = wktReader.read(dto.getGeometryWkt());
            geometry.setSRID(4326);

            GeoObject geoObject = new GeoObject();
            geoObject.setName(dto.getName());
            geoObject.setType(type);
            geoObject.setGeometry(geometry);
            
            if (dto.getParentId() != null) {
                GeoObject parent = geoObjectRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent not found: " + dto.getParentId()));
                geoObject.setParent(parent);
            }
            
            geoObject.setLodMin(dto.getLodMin());
            geoObject.setLodMax(dto.getLodMax());
            geoObject.setLabelPriority(dto.getLabelPriority());
            geoObject.setIsSegment(dto.getIsSegment());
            geoObject.setSegmentOrder(dto.getSegmentOrder());

            GeoObject saved = geoObjectRepository.save(geoObject);
            return convertToResponseDto(saved);
            
        } catch (ParseException e) {
            throw new RuntimeException("Invalid WKT geometry: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public GeoObjectResponseDto getGeoObject(Long id) {
        GeoObject geoObject = geoObjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Object not found: " + id));
        return convertToResponseDto(geoObject);
    }

    @Transactional(readOnly = true)
    public GeoObjectResponseDto getGeoObjectByName(String name) {
        GeoObject geoObject = geoObjectRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Object not found: " + name));
        return convertToResponseDto(geoObject);
    }

    @Transactional(readOnly = true)
    public GeoObject getById(Long id) {
        return geoObjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Object not found: " + id));
    }

    @Transactional(readOnly = true)
    public GeoObject getByName(String name) {
        return geoObjectRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Object not found: " + name));
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return geoObjectRepository.findByName(name).isPresent();
    }

    private GeoObjectResponseDto convertToResponseDto(GeoObject geoObject) {
        return GeoObjectResponseDto.builder()
                .id(geoObject.getId())
                .name(geoObject.getName())
                .typeId(geoObject.getType() != null ? geoObject.getType().getId() : null)
                .typeName(geoObject.getType() != null ? geoObject.getType().getName() : null)
                .geometryWkt(geoObject.getGeometry() != null ? geoObject.getGeometry().toText() : null)
                .parentId(geoObject.getParent() != null ? geoObject.getParent().getId() : null)
                .parentName(geoObject.getParent() != null ? geoObject.getParent().getName() : null)
                .lodMin(geoObject.getLodMin())
                .lodMax(geoObject.getLodMax())
                .labelPriority(geoObject.getLabelPriority())
                .isSegment(geoObject.getIsSegment())
                .segmentOrder(geoObject.getSegmentOrder())
                .build();
    }
}