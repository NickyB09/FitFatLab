package com.fitfatlab.fitfatlab_backend.modules.schedule.dto;

import com.fitfatlab.fitfatlab_backend.modules.schedule.model.TrainingSlotWeekday;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class TrainingSlotResponse {
    private UUID id;
    private UUID coachId;
    private UUID studentId;
    private TrainingSlotWeekday weekday;
    private LocalTime startTime;
    private String note;
    private LocalDateTime createdAt;
}
