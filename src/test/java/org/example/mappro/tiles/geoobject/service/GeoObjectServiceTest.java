package org.example.mappro.tiles.geoobject.service;

import org.example.mappro.tiles.geoobject.repository.GeoObjectRepository;
import org.example.mappro.tiles.geoobject.repository.RegionTypeCountProjection;
import org.example.mappro.tiles.geoobjecttype.repository.GeoObjectTypeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeoObjectServiceTest {

    @Test
    void groupsRegionCountsByGeoObjectType() {
        GeoObjectRepository geoObjectRepository = mock(GeoObjectRepository.class);
        GeoObjectTypeRepository typeRepository = mock(GeoObjectTypeRepository.class);
        GeoObjectService service = new GeoObjectService(geoObjectRepository, typeRepository);

        List<RegionTypeCountProjection> rows = List.of(
                row(10L, "North", 2L, "LINE_SECTION", "Line section", 12L),
                row(10L, "North", 3L, "VALVE_NODE", "Valve node", 5L),
                row(20L, "South", null, null, null, null)
        );
        when(geoObjectRepository.findRegionTypeCounts()).thenReturn(rows);

        var regions = service.getRegions();

        assertEquals(2, regions.size());
        assertEquals(10L, regions.getFirst().id());
        assertEquals(2, regions.getFirst().objectTypes().size());
        assertEquals("LINE_SECTION", regions.getFirst().objectTypes().getFirst().typeCode());
        assertEquals(12, regions.getFirst().objectTypes().getFirst().objectCount());
        assertTrue(regions.get(1).objectTypes().isEmpty());
    }

    private RegionTypeCountProjection row(
            Long regionId,
            String regionName,
            Long typeId,
            String typeCode,
            String typeName,
            Long objectCount
    ) {
        RegionTypeCountProjection row = mock(RegionTypeCountProjection.class);
        when(row.getRegionId()).thenReturn(regionId);
        when(row.getRegionName()).thenReturn(regionName);
        when(row.getTypeId()).thenReturn(typeId);
        when(row.getTypeCode()).thenReturn(typeCode);
        when(row.getTypeName()).thenReturn(typeName);
        when(row.getObjectCount()).thenReturn(objectCount);
        return row;
    }
}
