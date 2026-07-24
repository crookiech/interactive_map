package org.example.mappro.tiles.geoobjecttype.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeLodUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeResponseDto;
import org.example.mappro.tiles.geoobjecttype.model.GeoObjectType;
import org.example.mappro.tiles.geoobjecttype.GeometryTypeEnum;  // ← ИЗМЕНИТЬ ИМПОРТ
import org.example.mappro.tiles.geoobjecttype.repository.GeoObjectTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectTypeService {

    private final GeoObjectTypeRepository geoObjectTypeRepository;

    @PostConstruct
    @Transactional
    public void initDefaultGeoObjectTypes() {
        if (geoObjectTypeRepository.count() == 0) {
            createDefaultTypes();
        }
    }

    private void createDefaultTypes() {
        List<GeoObjectType> defaultTypes = Arrays.asList(
            createGeoObjectType("REGION", "Регион / зона филиала", GeometryTypeEnum.POLYGON, 0, 22, "#FF0000", "polygon", 1, true),
            createGeoObjectType("LINE_SECTION", "Линейная часть", GeometryTypeEnum.LINESTRING, 0, 22, "#00FF00", "line", 2, true),
            createGeoObjectType("VALVE_NODE", "Крановый узел", GeometryTypeEnum.POINT, 0, 22, "#0000FF", "point", 3, true),
            createGeoObjectType("COMPRESSOR_STATION", "Компрессорная станция", GeometryTypeEnum.POINT, 0, 22, "#FF00FF", "point", 4, true),
            createGeoObjectType("GAS_DISTRIBUTION_STATION", "Газораспределительная станция", GeometryTypeEnum.POINT, 0, 22, "#FFFF00", "point", 5, true),
            createGeoObjectType("GAS_PUMPING_UNIT", "Газоперекачивающий агрегат", GeometryTypeEnum.POINT, 0, 22, "#00FFFF", "point", 6, true)
        );
        
        geoObjectTypeRepository.saveAll(defaultTypes);
        log.info("Created {} default geo object types", defaultTypes.size());
    }

    private GeoObjectType createGeoObjectType(String code, String displayName, GeometryTypeEnum geometryType, Integer lodMin, Integer lodMax, String colorHex, String iconKey, Integer sortOrder, Boolean visibleByDefault) {
        GeoObjectType type = new GeoObjectType();
        type.setCode(code);
        type.setDisplayName(displayName);
        type.setGeometryType(geometryType);
        type.setLodMin(lodMin);
        type.setLodMax(lodMax);
        type.setColorHex(colorHex);
        type.setIconKey(iconKey);
        type.setSortOrder(sortOrder);
        type.setVisibleByDefault(visibleByDefault);
        return type;
    }

    @Transactional
    public GeoObjectTypeResponseDto createGeoObjectType(GeoObjectTypeCreateDto dto) {
        if (geoObjectTypeRepository.findByCode(dto.getCode()).isPresent()) {
            throw new IllegalArgumentException(
                "GeoObjectType with code '" + dto.getCode() + "' already exists"
            );
        }
        
        try {
            GeometryTypeEnum.valueOf(dto.getGeometryType().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid geometry type: " + dto.getGeometryType() + 
                ". Allowed values: " + GeometryTypeEnum.getAllowedValues()
            );
        }
        
        validateLodRange(dto.getLodMin(), dto.getLodMax());
        
        GeoObjectType geoObjectType = new GeoObjectType();
        geoObjectType.setCode(dto.getCode());
        geoObjectType.setDisplayName(dto.getDisplayName());
        geoObjectType.setGeometryType(dto.getGeometryType());
        geoObjectType.setLodMin(dto.getLodMin());
        geoObjectType.setLodMax(dto.getLodMax());
        geoObjectType.setColorHex(dto.getColorHex());
        geoObjectType.setIconKey(dto.getIconKey());
        geoObjectType.setSortOrder(dto.getSortOrder());
        geoObjectType.setVisibleByDefault(dto.getVisibleByDefault() != null ? dto.getVisibleByDefault() : true);

        GeoObjectType saved = geoObjectTypeRepository.save(geoObjectType);
        log.info("Created geo object type with id: {}", saved.getId());

        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<GeoObjectTypeResponseDto> getGeoObjectTypes() {
        return geoObjectTypeRepository.findAll().stream()
            .sorted(Comparator.comparing(GeoObjectType::getCode, String.CASE_INSENSITIVE_ORDER))
            .map(this::mapToResponseDto)
            .toList();
    }

    @Transactional
    @CacheEvict(value = "tiles", allEntries = true)
    public GeoObjectTypeResponseDto updateLod(Long id, GeoObjectTypeLodUpdateDto dto) {
        validateLodRange(dto.getLodMin(), dto.getLodMax());

        GeoObjectType geoObjectType = geoObjectTypeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("GeoObjectType with id " + id + " not found"));
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
            .code(geoObjectType.getCode())
            .displayName(geoObjectType.getDisplayName())
            .geometryType(geoObjectType.getGeometryType())
            .lodMin(geoObjectType.getLodMin())
            .lodMax(geoObjectType.getLodMax())
            .colorHex(geoObjectType.getColorHex())
            .iconKey(geoObjectType.getIconKey())
            .sortOrder(geoObjectType.getSortOrder())
            .visibleByDefault(geoObjectType.getVisibleByDefault())
            .build();
    }
}