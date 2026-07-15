package org.example.mappro.tiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "geo_object_violation")
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
