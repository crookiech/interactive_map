package org.example.mappro.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class SpatialIndexInitializer implements ApplicationRunner {

    private static final List<String> INDEX_STATEMENTS = List.of(
            """
            CREATE INDEX IF NOT EXISTS geo_objects_geometry_gix
            ON geo_objects USING GIST (geometry)
            """,
            """
            CREATE INDEX IF NOT EXISTS geo_objects_geometry_3857_gix
            ON geo_objects USING GIST (ST_Transform(geometry, 3857))
            """,
            """
            CREATE INDEX IF NOT EXISTS cities_geometry_gix
            ON cities USING GIST (geometry)
            """,
            """
            CREATE INDEX IF NOT EXISTS cities_geometry_3857_gix
            ON cities USING GIST (ST_Transform(geometry, 3857))
            """
    );

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        INDEX_STATEMENTS.forEach(jdbcTemplate::execute);
        log.info("Spatial GiST indexes are ready");
    }
}
