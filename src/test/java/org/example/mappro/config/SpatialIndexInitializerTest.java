package org.example.mappro.config;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpatialIndexInitializerTest {

    @Test
    void createsIndexesForStoredAndTransformedGeometries() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();

        new SpatialIndexInitializer(jdbcTemplate).run(null);

        assertEquals(4, jdbcTemplate.statements.size());
        assertTrue(jdbcTemplate.contains("geo_objects_geometry_gix", "GIST (geometry)"));
        assertTrue(jdbcTemplate.contains(
                "geo_objects_geometry_3857_gix",
                "GIST (ST_Transform(geometry, 3857))"
        ));
        assertTrue(jdbcTemplate.contains("cities_geometry_gix", "GIST (geometry)"));
        assertTrue(jdbcTemplate.contains(
                "cities_geometry_3857_gix",
                "GIST (ST_Transform(geometry, 3857))"
        ));
    }

    private static final class RecordingJdbcTemplate extends JdbcTemplate {
        private final List<String> statements = new ArrayList<>();

        @Override
        public void execute(String sql) {
            statements.add(sql);
        }

        private boolean contains(String indexName, String expression) {
            return statements.stream()
                    .anyMatch(sql -> sql.contains(indexName) && sql.contains(expression));
        }
    }
}
