package com.fitfatlab.fitfatlab_backend.modules.coaching.dto;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCoachStudentLinkStatusRequest {

    @NotNull
    private CoachStudentLinkStatus status;
}
