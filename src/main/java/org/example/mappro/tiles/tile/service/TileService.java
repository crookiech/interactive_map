package org.example.mappro.tiles.tile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.geoobject.dto.GeoObjectDto;
import org.example.mappro.tiles.geoobject.dto.GeoObjectTileProjectionDto;
import org.example.mappro.tiles.tile.dto.TileRequestDto;
import org.example.mappro.tiles.geoobject.repository.GeoObjectRepository;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TileService {

    private final GeoObjectRepository geoObjectRepository;
    private final ObjectMapper objectMapper;

    @Cacheable(value = "tiles", key = "#request")
    public FeatureCollection getTile(TileRequestDto request) {
        int z = request.getZ();
        double simplifyTolerance = computeSimplifyTolerance(z);
        boolean returnSegments = z >= 16;

        BoundingBox bbox = tileToBBox(request.getX(), request.getY(), z);

        // Единый метод
        List<GeoObjectTileProjectionDto> projections = geoObjectRepository.findObjectsForTile(
                z,
                bbox.xmin, bbox.ymin, bbox.xmax, bbox.ymax,
                request.getTypes(),
                request.getSeverities(),
                request.getFromDate(),
                request.getShowCities() != null ? request.getShowCities() : true,
                simplifyTolerance,
                returnSegments
        );

        List<GeoObjectDto> objects = projections.stream()
                .map(p -> GeoObjectDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .type(p.getType())
                        .geometryJson(p.getGeometryJson())
                        .parentId(p.getParentId())
                        .labelPriority(p.getLabelPriority())
                        .isSegment(p.getIsSegment())
                        .segmentOrder(p.getSegmentOrder())
                        .violationCount(p.getViolationCount())
                        .violationTypes(p.getViolationTypes())
                        .build())
                .collect(Collectors.toList());

        return buildFeatureCollection(objects, z, request.getLang());
    }

    private FeatureCollection buildFeatureCollection(List<GeoObjectDto> objects, int z, String lang) {
        FeatureCollection collection = new FeatureCollection();
        List<Feature> features = objects.stream()
                .map(dto -> buildFeature(dto, z, lang))
                .filter(f -> f != null)
                .collect(Collectors.toList());
        collection.setFeatures(features);
        return collection;
    }

    private Feature buildFeature(GeoObjectDto dto, int z, String lang) {
        Feature feature = new Feature();
        try {
            GeoJsonObject geometry = objectMapper.readValue(dto.getGeometryJson(), GeoJsonObject.class);
            feature.setGeometry(geometry);
        } catch (Exception e) {
            log.error("Ошибка парсинга геометрии для объекта id={}: {}", dto.getId(), e.getMessage());
            return null;
        }

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("id", dto.getId());
        props.put("name", dto.getName());
        props.put("type", dto.getType());
        props.put("violationCount", dto.getViolationCount());

        // Получаем массив типов нарушений и формируем топ-3, отфильтровывая null
        String[] types = dto.getViolationTypes();
        List<String> top3 = (types != null && types.length > 0)
                ? Arrays.stream(types)
                .filter(Objects::nonNull)   // исключаем null значения
                .limit(3)
                .collect(Collectors.toList())
                : List.of();
        props.put("violationTypes", top3);

        props.put("labelPriority", dto.getLabelPriority());

        if (z >= 16 && Boolean.TRUE.equals(dto.getIsSegment())) {
            props.put("parentId", dto.getParentId());
            props.put("segmentOrder", dto.getSegmentOrder());
        }

        feature.setProperties(props);
        return feature;
    }

    private double computeSimplifyTolerance(int z) {
        if (z <= 5) return 5000.0;
        else if (z <= 10) return 1000.0;
        else if (z <= 15) return 100.0;
        else return 0.0;
    }

    private BoundingBox tileToBBox(int x, int y, int z) {
        double n = Math.pow(2, z);
        double lonMin = x / n * 360.0 - 180.0;
        double lonMax = (x + 1) / n * 360.0 - 180.0;
        double latMin = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * (y + 1) / n))));
        double latMax = Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n))));
        return new BoundingBox(
                mercatorX(lonMin),
                mercatorY(latMin),
                mercatorX(lonMax),
                mercatorY(latMax)
        );
    }

    private double mercatorX(double lon) {
        return lon * 20037508.34 / 180.0;
    }

    private double mercatorY(double lat) {
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        return y * 20037508.34 / 180.0;
    }

    private static class BoundingBox {
        final double xmin, ymin, xmax, ymax;
        BoundingBox(double xmin, double ymin, double xmax, double ymax) {
            this.xmin = xmin;
            this.ymin = ymin;
            this.xmax = xmax;
            this.ymax = ymax;
        }
    }
}