package com.fitfatlab.fitfatlab_backend.modules.coaching.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCoachStudentLinkRequest {

    @NotNull
    private UUID studentId;
}
