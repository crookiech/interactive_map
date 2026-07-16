package org.example.mappro.tiles.geoobjectviolation.model;

import org.example.mappro.tiles.geoobject.model.GeoObject;
import org.example.mappro.tiles.violation.model.Violation;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "geo_object_violation", uniqueConstraints = @UniqueConstraint(columnNames = {"object_id", "violation_id"}))
@Data
public class GeoObjectViolation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private GeoObject geoObject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "violation_id")
    private Violation violation;
}
