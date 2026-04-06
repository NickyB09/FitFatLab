package com.fitfatlab.fitfatlab_backend.modules.exercise.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "muscle_group", nullable = false, length = 80)
    private String muscleGroup;

    @Column(length = 80)
    private String equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    // true = disponible para todos; false = creado por un trainer
    @Column(nullable = false)
    private boolean global = true;

    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}