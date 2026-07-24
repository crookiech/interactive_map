package org.example.mappro.tiles.city.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    @Column(name = "ascii_name", nullable = false)
    private String asciiName;

    @Column(nullable = false)
    private Long population;

    @Column(name = "label_priority", nullable = false)
    private Integer labelPriority;

    @Column(nullable = false)
    private Boolean visible = true;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point geometry;
}
