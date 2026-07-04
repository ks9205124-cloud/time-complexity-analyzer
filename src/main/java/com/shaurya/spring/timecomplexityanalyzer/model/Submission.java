package com.shaurya.spring.timecomplexityanalyzer.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "engine_data")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "user_string", columnDefinition = "TEXT")
    private String userString;

    @Column(name = "complexity")
    private String complexity;

    @Column(name = "depth")
    private String depth;
}
