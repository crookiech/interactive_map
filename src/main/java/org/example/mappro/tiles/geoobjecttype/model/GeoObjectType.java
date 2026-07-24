package org.example.mappro.tiles.geoobjecttype.model;

import org.example.mappro.tiles.geoobjecttype.GeometryTypeEnum;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "geo_object_types")
@Data
public class GeoObjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(name = "display_name")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "geometry_type")
    private GeometryTypeEnum geometryType;

    @Column(name = "lod_min")
    private Integer lodMin;

    @Column(name = "lod_max")
    private Integer lodMax;

    @Column(name = "color_hex")
    private String colorHex;

    @Column(name = "icon_key")
    private String iconKey;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "visible_by_default")
    private Boolean visibleByDefault;
}

