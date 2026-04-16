package com.fitfatlab.fitfatlab_backend.modules.routine.model;

import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assigned_routine_exercises")
@Getter
@Setter
@NoArgsConstructor
public class AssignedRoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_routine_id", nullable = false)
    private AssignedRoutine assignedRoutine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private int sets;

    @Column(nullable = false)
    private int reps;

    @Column(name = "rest_seconds", nullable = false)
    private int restSeconds;
}
