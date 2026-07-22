package org.example.mappro.tiles.geoobjecttype.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "geo_object_types")
@Data
public class GeoObjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //////////////////////////////////////////////////////////////////

    @Column(name = "lod_min")
    private Integer lodMin;

    @Column(name = "lod_max")
    private Integer lodMax;
}

