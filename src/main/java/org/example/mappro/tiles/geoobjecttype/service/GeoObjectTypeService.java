package org.example.mappro.tiles.geoobjecttype.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeResponseDto;
import org.example.mappro.tiles.geoobjecttype.model.GeoObjectType;
import org.example.mappro.tiles.geoobjecttype.repository.GeoObjectTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectTypeService {

    private final GeoObjectTypeRepository geoObjectTypeRepository;

    @Transactional
    public GeoObjectTypeResponseDto createGeoObjectType(GeoObjectTypeCreateDto dto) {
        if (geoObjectTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("GeoObjectType with type '" + dto.getName() + "' already exists");
        }
        
        GeoObjectType geoObjectType = new GeoObjectType();
        geoObjectType.setName(dto.getName());

        GeoObjectType saved = geoObjectTypeRepository.save(geoObjectType);
        log.info("Created violation with id: {}", saved.getId());

        return mapToResponseDto(saved);
    }

    private GeoObjectTypeResponseDto mapToResponseDto(GeoObjectType geoObjectType) {
        return GeoObjectTypeResponseDto.builder()
            .id(geoObjectType.getId())
            .name(geoObjectType.getName())
            .build();
    }
}
