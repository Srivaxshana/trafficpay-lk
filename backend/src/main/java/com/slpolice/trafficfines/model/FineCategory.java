package com.slpolice.trafficfines.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fine_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g., TC001

    @Column(nullable = false)
    private String name; // e.g., Speeding

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
