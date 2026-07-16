package org.example.mappro.tiles.geoobject.model;

import org.example.mappro.tiles.geoobjecttype.model.GeoObjectType;
import org.example.mappro.tiles.geoobjectviolation.model.GeoObjectViolation;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import java.util.List;

@Entity
@Table(name = "geo_objects")
@Data
public class GeoObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private GeoObject parent;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private GeoObjectType type;

    @Column(columnDefinition = "geometry")
    private Geometry geometry;

    @Column(name = "lod_min")
    private Integer lodMin;

    @Column(name = "lod_max")
    private Integer lodMax;

    @Column(name = "label_priority")
    private Integer labelPriority;

    @Column(name = "is_segment")
    private Boolean isSegment;

    @Column(name = "segment_order")
    private Integer segmentOrder;

    @OneToMany(mappedBy = "geoObject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GeoObjectViolation> geoObjectViolations;
}