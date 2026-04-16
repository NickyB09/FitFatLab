package com.fitfatlab.fitfatlab_backend.modules.routine.service;

import com.fitfatlab.fitfatlab_backend.modules.routine.dto.*;

import java.util.List;
import java.util.UUID;

public interface RoutinePlanningService {
    RoutineTemplateResponse createTemplate(UUID coachId, CreateRoutineTemplateRequest request);
    List<RoutineTemplateResponse> findCoachTemplates(UUID coachId);
    AssignedRoutineResponse assignRoutine(UUID coachId, AssignRoutineRequest request);
    List<AssignedRoutineResponse> findStudentAssignments(UUID studentId);
    List<AssignedRoutineResponse> findCoachAssignments(UUID coachId);
    AssignedRoutineResponse completeAssignment(UUID assignmentId, UUID studentId);
    Object reuseAssignment(UUID coachId, UUID assignmentId, ReuseAssignedRoutineRequest request);
}
