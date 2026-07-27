package org.example.mappro.tiles.geoobject.controller;

import org.example.mappro.tiles.geoobject.dto.RegionResponseDto;
import org.example.mappro.tiles.geoobject.dto.RegionObjectResponseDto;
import org.example.mappro.tiles.geoobject.dto.RegionObjectTypeCountDto;
import org.example.mappro.tiles.geoobject.service.GeoObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GeoObjectControllerTest {

    private GeoObjectService geoObjectService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        geoObjectService = mock(GeoObjectService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new GeoObjectController(geoObjectService)).build();
    }

    @Test
    void returnsLightweightRegionList() throws Exception {
        when(geoObjectService.getRegions()).thenReturn(List.of(
                new RegionResponseDto(10L, "North", List.of(
                        new RegionObjectTypeCountDto(2L, "LINE_SECTION", "Line section", 12),
                        new RegionObjectTypeCountDto(3L, "VALVE_NODE", "Valve node", 5)
                )),
                new RegionResponseDto(20L, "South", List.of())
        ));

        mockMvc.perform(get("/api/geo-objects/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("North"))
                .andExpect(jsonPath("$[0].objectTypes[0].typeId").value(2))
                .andExpect(jsonPath("$[0].objectTypes[0].typeCode").value("LINE_SECTION"))
                .andExpect(jsonPath("$[0].objectTypes[0].objectCount").value(12))
                .andExpect(jsonPath("$[0].objectTypes[1].typeCode").value("VALVE_NODE"))
                .andExpect(jsonPath("$[0].objectTypes[1].objectCount").value(5))
                .andExpect(jsonPath("$[1].id").value(20))
                .andExpect(jsonPath("$[1].name").value("South"))
                .andExpect(jsonPath("$[1].objectTypes").isEmpty());
    }

    @Test
    void returnsObjectsForExpandedRegion() throws Exception {
        when(geoObjectService.getRegionObjects(
                10L,
                2L,
                List.of("LINE_SECTION", "VALVE_NODE")
        )).thenReturn(List.of(
                new RegionObjectResponseDto(101L, "Pipeline", "LINE_SECTION"),
                new RegionObjectResponseDto(102L, "Valve", "VALVE_NODE")
        ));

        mockMvc.perform(get("/api/geo-objects/regions/10/object-types/2/objects")
                        .param("types", "LINE_SECTION,VALVE_NODE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].name").value("Pipeline"))
                .andExpect(jsonPath("$[0].typeCode").value("LINE_SECTION"))
                .andExpect(jsonPath("$[1].id").value(102))
                .andExpect(jsonPath("$[1].typeCode").value("VALVE_NODE"));
    }

}
