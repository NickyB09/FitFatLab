package com.fitfatlab.fitfatlab_backend.modules.coaching.dto;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CoachStudentLinkResponse {
    private UUID id;
    private UUID coachId;
    private String coachName;
    private UUID studentId;
    private String studentName;
    private CoachStudentLinkStatus status;
    private boolean allowStudentMealEdits;
    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
    private LocalDateTime endedAt;
}
