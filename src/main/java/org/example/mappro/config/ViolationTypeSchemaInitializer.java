package org.example.mappro.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class ViolationTypeSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("""
                ALTER TABLE violation_types
                ADD COLUMN IF NOT EXISTS code VARCHAR(100)
                """);
        jdbcTemplate.execute("""
                ALTER TABLE violation_types
                ADD COLUMN IF NOT EXISTS display_name VARCHAR(255)
                """);
        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.columns
                        WHERE table_schema = current_schema()
                          AND table_name = 'violation_types'
                          AND column_name = 'name'
                    ) THEN
                        UPDATE violation_types
                        SET code = COALESCE(code, name),
                            display_name = COALESCE(display_name, name);

                        ALTER TABLE violation_types
                        ALTER COLUMN name DROP NOT NULL;
                    END IF;
                END
                $$
                """);
        jdbcTemplate.execute("""
                UPDATE violation_types
                SET code = COALESCE(code, 'VIOLATION_TYPE_' || id),
                    display_name = COALESCE(display_name, code, 'Violation type ' || id)
                """);
        jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS violation_types_code_uix
                ON violation_types (code)
                """);
        jdbcTemplate.execute("""
                ALTER TABLE violation_types
                ALTER COLUMN code SET NOT NULL,
                ALTER COLUMN display_name SET NOT NULL
                """);
        log.info("Violation type code/display name schema is ready");
    }
}
