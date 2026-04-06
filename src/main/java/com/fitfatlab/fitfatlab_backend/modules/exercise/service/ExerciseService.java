package com.fitfatlab.fitfatlab_backend.modules.exercise.service;

import com.fitfatlab.fitfatlab_backend.modules.exercise.dto.ExerciseRequest;
import com.fitfatlab.fitfatlab_backend.modules.exercise.dto.ExerciseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExerciseService {
    ExerciseResponse create(ExerciseRequest request, boolean isGlobal);

    Page<ExerciseResponse> findAll(Pageable pageable);

    Page<ExerciseResponse> findByMuscleGroup(String muscleGroup, Pageable pageable);

    ExerciseResponse findById(UUID id);

    ExerciseResponse update(UUID id, ExerciseRequest request);

    void delete(UUID id);
}
