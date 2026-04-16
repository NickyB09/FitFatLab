package com.fitfatlab.fitfatlab_backend.modules.coaching.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CoachStudentLinkResponse;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CreateCoachStudentLinkRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.UpdateMealEditPermissionRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;

import java.util.List;
import java.util.UUID;

public interface CoachingService {

    CoachStudentLinkResponse createInvitation(UUID coachId, CreateCoachStudentLinkRequest request);

    CoachStudentLinkResponse updateStatus(UUID linkId, UUID actingUserId, CoachStudentLinkStatus status);

    CoachStudentLinkResponse updateMealEditPermission(UUID linkId, UUID coachId, UpdateMealEditPermissionRequest request);

    List<CoachStudentLinkResponse> findCoachLinks(UUID coachId);

    List<CoachStudentLinkResponse> findStudentLinks(UUID studentId);

    boolean canStudentEditMeals(UUID coachId, UUID studentId);
}
