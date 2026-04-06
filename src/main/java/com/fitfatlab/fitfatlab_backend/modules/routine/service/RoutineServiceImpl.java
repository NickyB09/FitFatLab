package com.fitfatlab.fitfatlab_backend.modules.routine.service;

import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import com.fitfatlab.fitfatlab_backend.modules.exercise.repository.ExerciseRepository;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineRequest;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineResponse;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.Routine;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineExercise;
import com.fitfatlab.fitfatlab_backend.modules.routine.repository.RoutineRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public RoutineResponse create(UUID userId, RoutineRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Routine routine = new Routine();
        routine.setUser(user);
        routine.setName(request.getName());
        routine.setDescription(request.getDescription());

        List<RoutineExercise> exerciseLinks = request.getExercises().stream()
                .map(req -> {
                    Exercise ex = exerciseRepository.findById(req.getExerciseId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Exercise not found: " + req.getExerciseId()));
                    RoutineExercise link = new RoutineExercise();
                    link.setRoutine(routine);
                    link.setExercise(ex);
                    link.setSets(req.getSets());
                    link.setReps(req.getReps());
                    link.setRestSeconds(req.getRestSeconds());
                    return link;
                }).toList();

        routine.getRoutineExercises().addAll(exerciseLinks);
        return toResponse(routineRepository.save(routine));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoutineResponse> findActiveByUser(UUID userId, Pageable pageable) {
        return routineRepository.findByUserIdAndActiveTrue(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineResponse findById(UUID routineId, UUID requestingUserId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found"));
        if (!routine.getUser().getId().equals(requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toResponse(routine);
    }

    @Override
    @Transactional
    public RoutineResponse update(UUID routineId, UUID userId, RoutineRequest request) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found"));
        if (!routine.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        routine.setName(request.getName());
        routine.setDescription(request.getDescription());
        routine.getRoutineExercises().clear();
        routine.getRoutineExercises().addAll(buildRoutineExercises(routine, request));

        return toResponse(routineRepository.save(routine));
    }

    @Override
    @Transactional
    public void deactivate(UUID routineId, UUID userId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found"));
        if (!routine.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        routine.setActive(false);
    }

    private List<RoutineExercise> buildRoutineExercises(Routine routine, RoutineRequest request) {
        return request.getExercises().stream()
                .map(req -> {
                    Exercise ex = exerciseRepository.findById(req.getExerciseId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Exercise not found: " + req.getExerciseId()));
                    RoutineExercise link = new RoutineExercise();
                    link.setRoutine(routine);
                    link.setExercise(ex);
                    link.setSets(req.getSets());
                    link.setReps(req.getReps());
                    link.setRestSeconds(req.getRestSeconds());
                    return link;
                }).toList();
    }

    private RoutineResponse toResponse(Routine r) {
        List<RoutineResponse.RoutineExerciseDetail> details = r.getRoutineExercises().stream()
                .map(re -> RoutineResponse.RoutineExerciseDetail.builder()
                        .exerciseId(re.getExercise().getId())
                        .exerciseName(re.getExercise().getName())
                        .muscleGroup(re.getExercise().getMuscleGroup())
                        .sets(re.getSets())
                        .reps(re.getReps())
                        .restSeconds(re.getRestSeconds())
                        .build())
                .toList();

        return RoutineResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .active(r.isActive())
                .createdAt(r.getCreatedAt())
                .exercises(details)
                .build();
    }
}
