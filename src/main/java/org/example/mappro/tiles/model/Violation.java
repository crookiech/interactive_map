package org.example.mappro.tiles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
@Data
public class Violation {
    @Id
    private Long id;
    @Column(name = "geo_object_id")
    private Long geoObjectId;
    private String type;
    private String severity;   // HIGH, MEDIUM, LOW
    private LocalDateTime date;
    private String description;
}
