package org.example.mappro.tiles.tileMVT.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TileMVTRepositoryImplTest {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private TileMVTRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        repository = new TileMVTRepositoryImpl(jdbcTemplate);
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(byte[].class)))
                .thenReturn(new byte[] {1, 2, 3});
    }

    @Test
    void filtersObjectsBySelectedRegionsUsingPostGis() {
        byte[] tile = repository.getTile(10, 20, 30, null, 1000, List.of(7L, 7L, 9L));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(byte[].class));

        String sql = sqlCaptor.getValue();
        SqlParameterSource params = paramsCaptor.getValue();

        assertTrue(sql.contains("AND EXISTS ("));
        assertTrue(sql.contains("region.id IN (:regionIds)"));
        assertTrue(sql.contains("region_type.code = 'REGION'"));
        assertTrue(sql.contains("ST_Intersects(go.geometry, region.geometry)"));
        assertTrue(sql.contains("ST_CoveredBy(go.geometry, region.geometry)"));
        assertTrue(sql.contains("got.color_hex AS color"));
        assertFalse(sql.contains("NOT IN (:excludedIds)"));
        assertEquals(List.of(7L, 9L), params.getValue("regionIds"));
        assertArrayEquals(new byte[] {1, 2, 3}, tile);
    }

    @Test
    void doesNotApplyRegionFilterWhenRegionsAreNotSelected() {
        repository.getTile(10, 20, 30, null, 1000, null);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(byte[].class));

        assertFalse(sqlCaptor.getValue().contains("region.id IN (:regionIds)"));
        assertFalse(paramsCaptor.getValue().hasValue("regionIds"));
    }
}
