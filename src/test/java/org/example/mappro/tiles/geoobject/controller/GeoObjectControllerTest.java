package org.example.mappro.tiles.geoobject.controller;

import org.example.mappro.tiles.geoobject.dto.RegionResponseDto;
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
                new RegionResponseDto(10L, "North"),
                new RegionResponseDto(20L, "South")
        ));

        mockMvc.perform(get("/api/geo-objects/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("North"))
                .andExpect(jsonPath("$[1].id").value(20))
                .andExpect(jsonPath("$[1].name").value("South"));
    }
}
