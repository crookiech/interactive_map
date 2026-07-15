package org.example.mappro.tiles.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "violations")
@Data
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ViolationType type;

    private String severity;
    private LocalDateTime date;
    private String description;
    private String status;

    @OneToMany(mappedBy = "violation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GeoObjectViolation> geoObjectViolations;
}

