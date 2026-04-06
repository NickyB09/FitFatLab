package com.fitfatlab.fitfatlab_backend.modules.exercise.repository;

import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    Page<Exercise> findByMuscleGroupIgnoreCaseAndGlobalTrue(String muscleGroup, Pageable pageable);

    Page<Exercise> findByGlobalTrue(Pageable pageable);
}