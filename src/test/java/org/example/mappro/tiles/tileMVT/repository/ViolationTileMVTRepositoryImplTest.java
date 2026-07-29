package org.example.mappro.tiles.tileMVT.repository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViolationTileMVTRepositoryImplTest {

    @Test
    void aggregatesViolationsIntoOneMvtFeaturePerGeoObject() {
        NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(byte[].class)))
            .thenReturn(new byte[] {1});
        ViolationTileMVTRepositoryImpl repository = new ViolationTileMVTRepositoryImpl(jdbcTemplate);

        repository.getViolationTile(
            5,
            20,
            10,
            List.of("APK", "OPO"),
            List.of("HIGH"),
            LocalDate.of(2026, 7, 1),
            false
        );

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sql.capture(), any(SqlParameterSource.class), eq(byte[].class));
        String normalizedSql = sql.getValue().replaceAll("\\s+", " ");

        assertTrue(normalizedSql.contains("GROUP BY object_id, violation_type_code, violation_type_name"));
        assertTrue(normalizedSql.contains("GROUP BY object_id"));
        assertTrue(normalizedSql.contains("SUM(count)::bigint AS violation_count"));
        assertTrue(normalizedSql.contains("'type', violation_type_code"));
        assertTrue(normalizedSql.contains("'count', count"));
        assertTrue(normalizedSql.contains("vbo.violations::text AS types"));
        assertTrue(normalizedSql.contains("vbo.object_id AS id"));
        assertFalse(normalizedSql.contains("primary_type"));
        assertFalse(normalizedSql.contains("primary_type_name"));
        assertTrue(normalizedSql.contains("ST_Transform(go.geometry, 3857)"));
        assertTrue(normalizedSql.contains("vt.code = ANY(CAST(:types AS text[]))"));
        assertTrue(normalizedSql.contains("v.severity = ANY(CAST(:severities AS text[]))"));
        assertTrue(normalizedSql.contains("v.date >= :fromDate"));
        assertTrue(normalizedSql.contains("got.code != 'CITY'"));
    }
}
