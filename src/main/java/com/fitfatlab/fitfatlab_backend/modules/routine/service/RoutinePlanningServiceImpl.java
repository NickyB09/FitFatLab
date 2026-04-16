package com.fitfatlab.fitfatlab_backend.modules.routine.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import com.fitfatlab.fitfatlab_backend.modules.exercise.repository.ExerciseRepository;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.*;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.*;
import com.fitfatlab.fitfatlab_backend.modules.routine.repository.AssignedRoutineRepository;
import com.fitfatlab.fitfatlab_backend.modules.routine.repository.RoutineTemplateRepository;
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
public class RoutinePlanningServiceImpl implements RoutinePlanningService {

    private final RoutineTemplateRepository routineTemplateRepository;
    private final AssignedRoutineRepository assignedRoutineRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final CoachStudentLinkRepository coachStudentLinkRepository;

    @Override
    @Transactional
    public RoutineTemplateResponse createTemplate(UUID coachId, CreateRoutineTemplateRequest request) {
        User coach = getCoach(coachId);

        RoutineTemplate template = new RoutineTemplate();
        template.setCoach(coach);
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setTemplateType(request.getTemplateType());
        template.getExercises().addAll(request.getExercises().stream()
                .map(item -> toTemplateExercise(template, item))
                .toList());

        return toTemplateResponse(routineTemplateRepository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoutineTemplateResponse> findCoachTemplates(UUID coachId) {
        return routineTemplateRepository.findByCoachIdOrderByCreatedAtDesc(coachId)
                .stream()
                .map(this::toTemplateResponse)
                .toList();
    }

    @Override
    @Transactional
    public AssignedRoutineResponse assignRoutine(UUID coachId, AssignRoutineRequest request) {
        User coach = getCoach(coachId);
        User student = getStudent(request.getStudentId());
        assertActiveRelationship(coachId, student.getId());

        AssignedRoutine assignedRoutine = new AssignedRoutine();
        assignedRoutine.setCoach(coach);
        assignedRoutine.setStudent(student);
        assignedRoutine.setName(resolveAssignmentName(request));
        assignedRoutine.setDescription(request.getDescription());
        assignedRoutine.setPeriodType(request.getPeriodType());
        assignedRoutine.setScheduledDate(request.getScheduledDate());
        assignedRoutine.setStatus(AssignedRoutineStatus.ASSIGNED);

        if (request.getTemplateId() != null) {
            RoutineTemplate template = getTemplate(request.getTemplateId());
            if (!template.getCoach().getId().equals(coachId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Template does not belong to the authenticated coach");
            }
            assignedRoutine.setTemplate(template);
            if (assignedRoutine.getName() == null || assignedRoutine.getName().isBlank()) {
                assignedRoutine.setName(template.getName());
            }
            if (assignedRoutine.getDescription() == null) {
                assignedRoutine.setDescription(template.getDescription());
            }
            assignedRoutine.getExercises().addAll(template.getExercises().stream()
                    .map(item -> toAssignedExercise(assignedRoutine, item.getExercise(), item.getSets(), item.getReps(), item.getRestSeconds()))
                    .toList());
        } else {
            if (request.getExercises() == null || request.getExercises().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignments require exercises or a template");
            }
            assignedRoutine.getExercises().addAll(request.getExercises().stream()
                    .map(item -> toAssignedExercise(assignedRoutine, getExercise(item.getExerciseId()), item.getSets(), item.getReps(), item.getRestSeconds()))
                    .toList());
        }

        return toAssignedResponse(assignedRoutineRepository.save(assignedRoutine));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignedRoutineResponse> findStudentAssignments(UUID studentId) {
        return assignedRoutineRepository.findByStudentIdOrderByScheduledDateDesc(studentId).stream()
                .map(this::toAssignedResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignedRoutineResponse> findCoachAssignments(UUID coachId) {
        return assignedRoutineRepository.findByCoachIdOrderByScheduledDateDesc(coachId).stream()
                .map(this::toAssignedResponse)
                .toList();
    }

    @Override
    @Transactional
    public AssignedRoutineResponse completeAssignment(UUID assignmentId, UUID studentId) {
        AssignedRoutine assignedRoutine = getAssignedRoutine(assignmentId);
        if (!assignedRoutine.getStudent().getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned student can complete the routine");
        }
        assignedRoutine.setStatus(AssignedRoutineStatus.COMPLETED);
        assignedRoutine.setCompletedAt(LocalDateTime.now());
        return toAssignedResponse(assignedRoutineRepository.save(assignedRoutine));
    }

    @Override
    @Transactional
    public Object reuseAssignment(UUID coachId, UUID assignmentId, ReuseAssignedRoutineRequest request) {
        AssignedRoutine source = getAssignedRoutine(assignmentId);
        if (!source.getCoach().getId().equals(coachId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assignment owner can reuse it");
        }

        return switch (request.getReuseMode()) {
            case TEMPLATE -> reuseAsTemplate(source, request);
            case ASSIGNMENT -> reuseAsAssignment(coachId, source, request);
        };
    }

    private RoutineTemplateResponse reuseAsTemplate(AssignedRoutine source, ReuseAssignedRoutineRequest request) {
        RoutineTemplate template = new RoutineTemplate();
        template.setCoach(source.getCoach());
        template.setName(request.getName() != null ? request.getName() : source.getName());
        template.setDescription(request.getDescription() != null ? request.getDescription() : source.getDescription());
        template.setTemplateType(request.getTemplateType() != null ? request.getTemplateType() : RoutineTemplateType.CUSTOM);
        template.getExercises().addAll(source.getExercises().stream()
                .map(item -> toTemplateExercise(template, item.getExercise(), item.getSets(), item.getReps(), item.getRestSeconds()))
                .toList());
        return toTemplateResponse(routineTemplateRepository.save(template));
    }

    private AssignedRoutineResponse reuseAsAssignment(UUID coachId, AssignedRoutine source, ReuseAssignedRoutineRequest request) {
        if (request.getStudentId() == null || request.getPeriodType() == null || request.getScheduledDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reusing as assignment requires studentId, periodType and scheduledDate");
        }
        assertActiveRelationship(coachId, request.getStudentId());
        User student = getStudent(request.getStudentId());

        AssignedRoutine assignedRoutine = new AssignedRoutine();
        assignedRoutine.setCoach(source.getCoach());
        assignedRoutine.setStudent(student);
        assignedRoutine.setTemplate(source.getTemplate());
        assignedRoutine.setName(request.getName() != null ? request.getName() : source.getName());
        assignedRoutine.setDescription(request.getDescription() != null ? request.getDescription() : source.getDescription());
        assignedRoutine.setPeriodType(request.getPeriodType());
        assignedRoutine.setScheduledDate(request.getScheduledDate());
        assignedRoutine.setStatus(AssignedRoutineStatus.ASSIGNED);
        assignedRoutine.getExercises().addAll(source.getExercises().stream()
                .map(item -> toAssignedExercise(assignedRoutine, item.getExercise(), item.getSets(), item.getReps(), item.getRestSeconds()))
                .toList());
        return toAssignedResponse(assignedRoutineRepository.save(assignedRoutine));
    }

    private void assertActiveRelationship(UUID coachId, UUID studentId) {
        if (!coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Active coach-student relationship required");
        }
    }

    private String resolveAssignmentName(AssignRoutineRequest request) {
        return request.getName() != null ? request.getName() : "Assigned Routine";
    }

    private RoutineTemplateExercise toTemplateExercise(RoutineTemplate template, CreateRoutineTemplateRequest.TemplateExerciseRequest item) {
        return toTemplateExercise(template, getExercise(item.getExerciseId()), item.getSets(), item.getReps(), item.getRestSeconds());
    }

    private RoutineTemplateExercise toTemplateExercise(RoutineTemplate template, Exercise exercise, int sets, int reps, int restSeconds) {
        RoutineTemplateExercise routineExercise = new RoutineTemplateExercise();
        routineExercise.setRoutineTemplate(template);
        routineExercise.setExercise(exercise);
        routineExercise.setSets(sets);
        routineExercise.setReps(reps);
        routineExercise.setRestSeconds(restSeconds);
        return routineExercise;
    }

    private AssignedRoutineExercise toAssignedExercise(AssignedRoutine routine, Exercise exercise, int sets, int reps, int restSeconds) {
        AssignedRoutineExercise assignedExercise = new AssignedRoutineExercise();
        assignedExercise.setAssignedRoutine(routine);
        assignedExercise.setExercise(exercise);
        assignedExercise.setSets(sets);
        assignedExercise.setReps(reps);
        assignedExercise.setRestSeconds(restSeconds);
        return assignedExercise;
    }

    private User getCoach(UUID coachId) {
        User coach = getUser(coachId);
        assertRole(coach, Role.RoleName.ROLE_TRAINER, "Only trainers can manage routine planning");
        return coach;
    }

    private User getStudent(UUID studentId) {
        return getUser(studentId);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }

    private Exercise getExercise(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found: " + exerciseId));
    }

    private RoutineTemplate getTemplate(UUID templateId) {
        return routineTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine template not found: " + templateId));
    }

    private AssignedRoutine getAssignedRoutine(UUID assignmentId) {
        return assignedRoutineRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigned routine not found: " + assignmentId));
    }

    private void assertRole(User user, Role.RoleName roleName, String message) {
        boolean hasRole = user.getRoles().stream().anyMatch(role -> role.getName() == roleName);
        if (!hasRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    private RoutineTemplateResponse toTemplateResponse(RoutineTemplate template) {
        return RoutineTemplateResponse.builder()
                .id(template.getId())
                .coachId(template.getCoach().getId())
                .name(template.getName())
                .description(template.getDescription())
                .templateType(template.getTemplateType())
                .createdAt(template.getCreatedAt())
                .exercises(template.getExercises().stream()
                        .map(item -> RoutineTemplateResponse.RoutineTemplateExerciseResponse.builder()
                                .exerciseId(item.getExercise().getId())
                                .exerciseName(item.getExercise().getName())
                                .sets(item.getSets())
                                .reps(item.getReps())
                                .restSeconds(item.getRestSeconds())
                                .build())
                        .toList())
                .build();
    }

    private AssignedRoutineResponse toAssignedResponse(AssignedRoutine routine) {
        return AssignedRoutineResponse.builder()
                .id(routine.getId())
                .coachId(routine.getCoach().getId())
                .studentId(routine.getStudent().getId())
                .templateId(routine.getTemplate() != null ? routine.getTemplate().getId() : null)
                .name(routine.getName())
                .description(routine.getDescription())
                .periodType(routine.getPeriodType())
                .status(routine.getStatus())
                .scheduledDate(routine.getScheduledDate())
                .createdAt(routine.getCreatedAt())
                .completedAt(routine.getCompletedAt())
                .exercises(routine.getExercises().stream()
                        .map(item -> AssignedRoutineResponse.AssignedRoutineExerciseResponse.builder()
                                .exerciseId(item.getExercise() != null ? item.getExercise().getId() : null)
                                .exerciseName(item.getExercise() != null ? item.getExercise().getName() : null)
                                .sets(item.getSets())
                                .reps(item.getReps())
                                .restSeconds(item.getRestSeconds())
                                .build())
                        .toList())
                .build();
    }
}
