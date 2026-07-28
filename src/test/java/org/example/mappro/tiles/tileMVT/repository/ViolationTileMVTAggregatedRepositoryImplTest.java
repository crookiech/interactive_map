package org.example.mappro.tiles.tileMVT.repository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViolationTileMVTAggregatedRepositoryImplTest {

    @Test
    void restrictsResolvedParentNameToRegionObjects() {
        NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(byte[].class)))
            .thenReturn(new byte[] {1});
        ViolationTileMVTAggregatedRepositoryImpl repository =
            new ViolationTileMVTAggregatedRepositoryImpl(jdbcTemplate);

        repository.getViolationTile(4, 8, 5, null);

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sql.capture(), any(SqlParameterSource.class), eq(byte[].class));
        String normalizedSql = sql.getValue().replaceAll("\\s+", " ");
        assertTrue(normalizedSql.contains(
            "WHERE (pt.id = vit.object_id OR pt.id = vit.parent_id) AND pt.type_code = 'REGION'"
        ));
        assertTrue(normalizedSql.contains("vt.code AS violation_type_code"));
        assertTrue(normalizedSql.contains("vt.display_name AS violation_type_name"));
        assertTrue(normalizedSql.contains("violation_type_code AS type"));
        assertTrue(normalizedSql.contains("violation_type_name AS \"typeName\""));
    }
}
