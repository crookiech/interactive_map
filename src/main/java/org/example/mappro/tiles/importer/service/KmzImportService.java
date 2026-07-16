package org.example.mappro.tiles.importer.service;

import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.importer.model.KmzImportException;
import org.example.mappro.tiles.importer.service.RegionGeometrySplitter.GeometryPart;
import org.example.mappro.tiles.importer.dto.KmzImportResponse;
import org.example.mappro.tiles.importer.model.ImportedPlacemark;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.GeoObjectType;
import org.example.mappro.tiles.repository.GeoObjectRepository;
import org.example.mappro.tiles.repository.GeoObjectTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KmzImportService {
    private final KmzParser parser;
    private final KmzObjectTypeResolver typeResolver;
    private final RegionGeometrySplitter splitter;
    private final GeoObjectRepository geoObjectRepository;
    private final GeoObjectTypeRepository typeRepository;

    @Transactional
    public KmzImportResponse importFile(MultipartFile file) {
        List<GeoObject> regions = geoObjectRepository.findAllByType_NameOrderByIdAsc("region");
        List<ImportedPlacemark> placemarks = parser.parse(file);
        Map<String, GeoObjectType> types = new HashMap<>();
        List<String> warnings = new ArrayList<>();
        int created = 0;
        int skipped = 0;

        for (ImportedPlacemark placemark : placemarks) {
            var resolvedType = typeResolver.resolve(placemark);
            if (resolvedType.isEmpty()) {
                skipped++;
                warnings.add("Unsupported object skipped: " + placemark.name());
                continue;
            }
            String typeName = resolvedType.get();
            GeoObjectType type = types.computeIfAbsent(typeName, this::requiredType);
            List<GeometryPart> parts = splitter.split(placemark.geometry(), regions);
            if (parts.isEmpty()) {
                skipped++;
                warnings.add("Empty geometry skipped: " + placemark.name());
                continue;
            }

            boolean segmented = parts.size() > 1;
            for (int index = 0; index < parts.size(); index++) {
                GeometryPart part = parts.get(index);
                GeoObject object = new GeoObject();
                object.setName(segmented ? placemark.name() + " [" + (index + 1) + "]" : placemark.name());
                object.setType(type);
                object.setGeometry(part.geometry());
                object.setParent(part.region());
                object.setLodMin(0);
                object.setLodMax(22);
                object.setLabelPriority(1);
                object.setIsSegment(segmented);
                object.setSegmentOrder(segmented ? index + 1 : null);
                geoObjectRepository.save(object);
                created++;
            }
        }
        return new KmzImportResponse(placemarks.size(), created, skipped, List.copyOf(warnings));
    }

    private GeoObjectType requiredType(String typeName) {
        return typeRepository.findByName(typeName)
                .orElseThrow(() -> new KmzImportException("Object type not found: " + typeName));
    }
}
