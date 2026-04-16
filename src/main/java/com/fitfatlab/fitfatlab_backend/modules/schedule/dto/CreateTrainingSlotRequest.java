package com.fitfatlab.fitfatlab_backend.modules.schedule.dto;

import com.fitfatlab.fitfatlab_backend.modules.schedule.model.TrainingSlotWeekday;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CreateTrainingSlotRequest {
    @NotNull
    private UUID studentId;
    @NotNull
    private TrainingSlotWeekday weekday;
    @NotNull
    private LocalTime startTime;
    @Size(max = 200)
    private String note;
}
