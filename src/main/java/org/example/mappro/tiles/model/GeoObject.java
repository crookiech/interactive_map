package org.example.mappro.tiles.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "geo_objects")
@Data
public class GeoObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    @Column(columnDefinition = "geometry")   // без указания SRID
    private Geometry geometry;
    private Long parentId;
    private Integer lodMin;
    private Integer lodMax;
    private Integer labelPriority;
    private Boolean isSegment;
    @Column(name = "segment_order")
    private Integer segmentOrder;
}