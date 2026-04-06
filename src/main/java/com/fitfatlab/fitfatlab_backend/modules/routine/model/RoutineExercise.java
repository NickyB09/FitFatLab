package com.fitfatlab.fitfatlab_backend.modules.routine.model;

import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routine_exercises")
@Getter @Setter @NoArgsConstructor
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(nullable = false)
    private int sets;

    @Column(nullable = false)
    private int reps;

    @Column(name = "rest_seconds")
    private int restSeconds;
}
