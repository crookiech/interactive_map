package org.example.mappro.tiles.geoobject.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GeoObjectRepositoryCustomImpl implements GeoObjectRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Long> findDescendantIdsByGeometry(Long parentId) {
        String sql = """
            WITH RECURSIVE descendants AS (
                -- Начинаем с родительского объекта
                SELECT id, geometry, parent_id, 0 as depth
                FROM geo_objects 
                WHERE id = :parentId
                
                UNION ALL
                
                SELECT go.id, go.geometry, go.parent_id, d.depth + 1
                FROM geo_objects go
                INNER JOIN descendants d ON 
                    ST_Within(
                        ST_Centroid(ST_Transform(go.geometry, 3857)),
                        ST_Transform(d.geometry, 3857)
                    )
                    AND go.id != d.id
                    AND go.parent_id IS NOT NULL
                    AND d.depth < 10
            )
            SELECT id FROM descendants WHERE id != :parentId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentId", parentId);

        try {
            return jdbcTemplate.queryForList(sql, params, Long.class);
        } catch (Exception e) {
            log.error("Error finding descendants by geometry for parentId {}: {}", parentId, e.getMessage());
            return List.of(); // Возвращаем пустой список при ошибке
        }
    }
}