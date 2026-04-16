package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutinePeriodType;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutineStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AssignedRoutineResponse {
    private UUID id;
    private UUID coachId;
    private UUID studentId;
    private UUID templateId;
    private String name;
    private String description;
    private AssignedRoutinePeriodType periodType;
    private AssignedRoutineStatus status;
    private LocalDate scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<AssignedRoutineExerciseResponse> exercises;

    @Getter
    @Builder
    public static class AssignedRoutineExerciseResponse {
        private UUID exerciseId;
        private String exerciseName;
        private int sets;
        private int reps;
        private int restSeconds;
    }
}
