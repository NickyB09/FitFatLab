package com.fitfatlab.fitfatlab_backend.modules.exercise.service;

import com.fitfatlab.fitfatlab_backend.modules.exercise.dto.ExerciseRequest;
import com.fitfatlab.fitfatlab_backend.modules.exercise.dto.ExerciseResponse;
import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import com.fitfatlab.fitfatlab_backend.modules.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public ExerciseResponse create(ExerciseRequest request, boolean isGlobal) {
        Exercise exercise = new Exercise();
        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setEquipment(request.getEquipment());
        exercise.setDifficulty(request.getDifficulty());
        exercise.setDescription(request.getDescription());
        exercise.setGlobal(isGlobal);
        return toResponse(exerciseRepository.save(exercise));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExerciseResponse> findAll(Pageable pageable) {
        return exerciseRepository.findByGlobalTrue(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExerciseResponse> findByMuscleGroup(String muscleGroup, Pageable pageable) {
        return exerciseRepository
                .findByMuscleGroupIgnoreCaseAndGlobalTrue(muscleGroup, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponse findById(UUID id) {
        return exerciseRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    }

    @Override
    @Transactional
    public ExerciseResponse update(UUID id, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setEquipment(request.getEquipment());
        exercise.setDifficulty(request.getDifficulty());
        exercise.setDescription(request.getDescription());
        return toResponse(exerciseRepository.save(exercise));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
        exerciseRepository.delete(exercise);
    }

    private ExerciseResponse toResponse(Exercise e) {
        return ExerciseResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .muscleGroup(e.getMuscleGroup())
                .equipment(e.getEquipment())
                .difficulty(e.getDifficulty())
                .description(e.getDescription())
                .global(e.isGlobal())
                .build();
    }
}
