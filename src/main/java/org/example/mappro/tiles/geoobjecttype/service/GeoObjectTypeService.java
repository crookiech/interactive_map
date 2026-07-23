package org.example.mappro.tiles.geoobjecttype.service;

import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeLodUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeResponseDto;
import org.example.mappro.tiles.geoobjecttype.model.GeoObjectType;
import org.example.mappro.tiles.geoobjecttype.repository.GeoObjectTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectTypeService {

    private final GeoObjectTypeRepository geoObjectTypeRepository;

    @Transactional(readOnly = true)
    public List<GeoObjectTypeResponseDto> getGeoObjectTypes() {
        return geoObjectTypeRepository.findAll().stream()
            .sorted(Comparator.comparing(GeoObjectType::getName, String.CASE_INSENSITIVE_ORDER))
            .map(this::mapToResponseDto)
            .toList();
    }

    @Transactional
    public GeoObjectTypeResponseDto createGeoObjectType(GeoObjectTypeCreateDto dto) {
        if (geoObjectTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("GeoObjectType with type '" + dto.getName() + "' already exists");
        }
        validateLodRange(dto.getLodMin(), dto.getLodMax());

        GeoObjectType geoObjectType = new GeoObjectType();
        geoObjectType.setName(dto.getName());
        geoObjectType.setLodMin(dto.getLodMin());
        geoObjectType.setLodMax(dto.getLodMax());

        GeoObjectType saved = geoObjectTypeRepository.save(geoObjectType);
        log.info("Created geo object type with id: {}", saved.getId());

        return mapToResponseDto(saved);
    }

    @Transactional
    @CacheEvict(value = "tiles", allEntries = true)
    public GeoObjectTypeResponseDto updateLod(Long id, GeoObjectTypeLodUpdateDto dto) {
        validateLodRange(dto.getLodMin(), dto.getLodMax());

        GeoObjectType geoObjectType = geoObjectTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("GeoObjectType with id " + id + " not found"));
        geoObjectType.setLodMin(dto.getLodMin());
        geoObjectType.setLodMax(dto.getLodMax());

        GeoObjectType saved = geoObjectTypeRepository.save(geoObjectType);
        log.info("Updated LOD for geo object type with id: {}", saved.getId());
        return mapToResponseDto(saved);
    }

    private void validateLodRange(Integer lodMin, Integer lodMax) {
        if (lodMin != null && lodMax != null && lodMin > lodMax) {
            throw new IllegalArgumentException("lodMin must not exceed lodMax");
        }
    }

    private GeoObjectTypeResponseDto mapToResponseDto(GeoObjectType geoObjectType) {
        return GeoObjectTypeResponseDto.builder()
            .id(geoObjectType.getId())
            .name(geoObjectType.getName())
            .lodMin(geoObjectType.getLodMin())
            .lodMax(geoObjectType.getLodMax())
            .build();
    }
}