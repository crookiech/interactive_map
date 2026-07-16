package org.example.mappro.tiles.geoobjecttype.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.mappro.tiles.geoobjecttype.model.GeoObjectType;
import org.example.mappro.tiles.geoobjecttype.repository.GeoObjectTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectTypeService {

    private final GeoObjectTypeRepository geoObjectTypeRepository;

    @Transactional
    public GeoObjectType createGeoObjectType(GeoObjectType dto) {
        if (geoObjectTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("GeoObjectType with type '" + dto.getName() + "' already exists");
        }
        
        GeoObjectType geoObjectType = new GeoObjectType();
        geoObjectType.setName(dto.getName());

        return geoObjectTypeRepository.save(geoObjectType);
    }
}
