package org.example.mappro.config;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViolationTypeSchemaInitializerTest {

    @Test
    void migratesNameToCodeAndDisplayName() {
        RecordingJdbcTemplate jdbcTemplate = new RecordingJdbcTemplate();

        new ViolationTypeSchemaInitializer(jdbcTemplate).run(null);

        assertEquals(6, jdbcTemplate.statements.size());
        assertTrue(jdbcTemplate.contains("ADD COLUMN IF NOT EXISTS code"));
        assertTrue(jdbcTemplate.contains("ADD COLUMN IF NOT EXISTS display_name"));
        assertTrue(jdbcTemplate.contains("SET code = COALESCE(code, name)"));
        assertTrue(jdbcTemplate.contains("ALTER COLUMN name DROP NOT NULL"));
        assertTrue(jdbcTemplate.contains("violation_types_code_uix"));
        assertTrue(jdbcTemplate.contains("ALTER COLUMN code SET NOT NULL"));
    }

    private static final class RecordingJdbcTemplate extends JdbcTemplate {
        private final List<String> statements = new ArrayList<>();

        @Override
        public void execute(String sql) {
            statements.add(sql);
        }

        private boolean contains(String fragment) {
            return statements.stream().anyMatch(sql -> sql.contains(fragment));
        }
    }
}
