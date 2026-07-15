package org.example.mappro.tiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "violation_types")
@Data
public class ViolationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
