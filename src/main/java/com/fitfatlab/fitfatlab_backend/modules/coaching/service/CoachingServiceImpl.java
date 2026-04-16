package com.fitfatlab.fitfatlab_backend.modules.coaching.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CoachStudentLinkResponse;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CreateCoachStudentLinkRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.UpdateMealEditPermissionRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLink;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoachingServiceImpl implements CoachingService {

    private final CoachStudentLinkRepository coachStudentLinkRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CoachStudentLinkResponse createInvitation(UUID coachId, CreateCoachStudentLinkRequest request) {
        User coach = getRequiredUser(coachId);
        User student = getRequiredUser(request.getStudentId());

        assertHasRole(coach, Role.RoleName.ROLE_TRAINER, "Only trainers can create coach-student links");
        if (coach.getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coach and student must be different users");
        }
        if (coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, student.getId(), CoachStudentLinkStatus.PENDING)
                || coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, student.getId(), CoachStudentLinkStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A pending or active relationship already exists");
        }

        CoachStudentLink link = new CoachStudentLink();
        link.setCoach(coach);
        link.setStudent(student);
        link.setStatus(CoachStudentLinkStatus.PENDING);
        link.setAllowStudentMealEdits(false);

        return toResponse(coachStudentLinkRepository.save(link));
    }

    @Override
    @Transactional
    public CoachStudentLinkResponse updateStatus(UUID linkId, UUID actingUserId, CoachStudentLinkStatus status) {
        CoachStudentLink link = getRequiredLink(linkId);

        switch (status) {
            case ACTIVE -> activateLink(link, actingUserId);
            case REJECTED -> rejectLink(link, actingUserId);
            case ENDED -> endLink(link, actingUserId);
            case PENDING -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot revert relationship to pending");
        }

        return toResponse(coachStudentLinkRepository.save(link));
    }

    @Override
    @Transactional
    public CoachStudentLinkResponse updateMealEditPermission(UUID linkId, UUID coachId, UpdateMealEditPermissionRequest request) {
        CoachStudentLink link = getRequiredLink(linkId);
        if (!link.getCoach().getId().equals(coachId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned coach can update meal permissions");
        }
        if (link.getStatus() != CoachStudentLinkStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meal edit permissions require an active relationship");
        }

        link.setAllowStudentMealEdits(request.isAllowStudentMealEdits());
        return toResponse(coachStudentLinkRepository.save(link));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachStudentLinkResponse> findCoachLinks(UUID coachId) {
        return coachStudentLinkRepository.findByCoachIdOrderByCreatedAtDesc(coachId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachStudentLinkResponse> findStudentLinks(UUID studentId) {
        return coachStudentLinkRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canStudentEditMeals(UUID coachId, UUID studentId) {
        return coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatusAndAllowStudentMealEditsTrue(
                coachId, studentId, CoachStudentLinkStatus.ACTIVE);
    }

    private void activateLink(CoachStudentLink link, UUID actingUserId) {
        if (!link.getStudent().getId().equals(actingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the invited student can accept the relationship");
        }
        if (link.getStatus() != CoachStudentLinkStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending relationships can be activated");
        }
        if (coachStudentLinkRepository.existsByStudentIdAndStatus(link.getStudent().getId(), CoachStudentLinkStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already has an active trainer");
        }

        link.setStatus(CoachStudentLinkStatus.ACTIVE);
        link.setActivatedAt(LocalDateTime.now());
        link.setEndedAt(null);
    }

    private void rejectLink(CoachStudentLink link, UUID actingUserId) {
        if (!link.getStudent().getId().equals(actingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the invited student can reject the relationship");
        }
        if (link.getStatus() != CoachStudentLinkStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending relationships can be rejected");
        }

        link.setStatus(CoachStudentLinkStatus.REJECTED);
        link.setEndedAt(LocalDateTime.now());
    }

    private void endLink(CoachStudentLink link, UUID actingUserId) {
        if (!link.getCoach().getId().equals(actingUserId) && !link.getStudent().getId().equals(actingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only participants can end the relationship");
        }
        if (link.getStatus() != CoachStudentLinkStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active relationships can be ended");
        }

        link.setStatus(CoachStudentLinkStatus.ENDED);
        link.setEndedAt(LocalDateTime.now());
        link.setAllowStudentMealEdits(false);
    }

    private User getRequiredUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }

    private CoachStudentLink getRequiredLink(UUID linkId) {
        return coachStudentLinkRepository.findById(linkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach-student link not found: " + linkId));
    }

    private void assertHasRole(User user, Role.RoleName roleName, String message) {
        boolean hasRole = user.getRoles().stream().anyMatch(role -> role.getName() == roleName);
        if (!hasRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    private CoachStudentLinkResponse toResponse(CoachStudentLink link) {
        return CoachStudentLinkResponse.builder()
                .id(link.getId())
                .coachId(link.getCoach().getId())
                .coachName(link.getCoach().getFullName())
                .studentId(link.getStudent().getId())
                .studentName(link.getStudent().getFullName())
                .status(link.getStatus())
                .allowStudentMealEdits(link.isAllowStudentMealEdits())
                .createdAt(link.getCreatedAt())
                .activatedAt(link.getActivatedAt())
                .endedAt(link.getEndedAt())
                .build();
    }
}
