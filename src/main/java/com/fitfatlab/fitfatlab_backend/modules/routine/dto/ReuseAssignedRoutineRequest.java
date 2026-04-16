package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutinePeriodType;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineTemplateType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ReuseAssignedRoutineRequest {

    @NotNull
    private ReuseMode reuseMode;

    private String name;
    private String description;
    private RoutineTemplateType templateType;
    private UUID studentId;
    private AssignedRoutinePeriodType periodType;
    private LocalDate scheduledDate;

    public enum ReuseMode {
        TEMPLATE,
        ASSIGNMENT
    }
}
