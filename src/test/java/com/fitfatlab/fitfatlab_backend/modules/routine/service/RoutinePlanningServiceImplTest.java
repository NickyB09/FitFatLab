package com.fitfatlab.fitfatlab_backend.modules.routine.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise;
import com.fitfatlab.fitfatlab_backend.modules.exercise.repository.ExerciseRepository;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.AssignRoutineRequest;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.CreateRoutineTemplateRequest;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.ReuseAssignedRoutineRequest;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutine;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutinePeriodType;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutineStatus;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineTemplate;
import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineTemplateType;
import com.fitfatlab.fitfatlab_backend.modules.routine.repository.AssignedRoutineRepository;
import com.fitfatlab.fitfatlab_backend.modules.routine.repository.RoutineTemplateRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutinePlanningServiceImplTest {

    @Mock
    private RoutineTemplateRepository routineTemplateRepository;

    @Mock
    private AssignedRoutineRepository assignedRoutineRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoachStudentLinkRepository coachStudentLinkRepository;

    @InjectMocks
    private RoutinePlanningServiceImpl routinePlanningService;

    @Test
    void shouldCreateGenericTemplateForTrainer() {
        UUID coachId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        Exercise exercise = exercise(exerciseId, "Bench Press");
        CreateRoutineTemplateRequest request = templateRequest(exerciseId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(routineTemplateRepository.save(any(RoutineTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = routinePlanningService.createTemplate(coachId, request);

        assertThat(response.getName()).isEqualTo("PPL Push");
        assertThat(response.getTemplateType()).isEqualTo(RoutineTemplateType.GENERIC);
        assertThat(response.getExercises()).hasSize(1);
        assertThat(response.getExercises().getFirst().getRestSeconds()).isEqualTo(90);
    }

    @Test
    void shouldAssignWeeklyRoutineWhenCoachHasActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);
        Exercise exercise = exercise(exerciseId, "Deadlift");
        AssignRoutineRequest request = assignmentRequest(studentId, exerciseId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE))
                .thenReturn(true);
        when(assignedRoutineRepository.save(any(AssignedRoutine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = routinePlanningService.assignRoutine(coachId, request);

        assertThat(response.getStudentId()).isEqualTo(studentId);
        assertThat(response.getPeriodType()).isEqualTo(AssignedRoutinePeriodType.WEEK);
        assertThat(response.getStatus()).isEqualTo(AssignedRoutineStatus.ASSIGNED);
        assertThat(response.getExercises().getFirst().getRestSeconds()).isEqualTo(120);
    }

    @Test
    void shouldRejectAssignmentWithoutActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);
        AssignRoutineRequest request = assignmentRequest(studentId, exerciseId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE))
                .thenReturn(false);

        assertThatThrownBy(() -> routinePlanningService.assignRoutine(coachId, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldMarkAssignedRoutineAsCompletedByStudent() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID assignmentId = UUID.randomUUID();
        AssignedRoutine assignment = assignedRoutine(assignmentId, coachId, studentId);

        when(assignedRoutineRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignedRoutineRepository.save(assignment)).thenReturn(assignment);

        var response = routinePlanningService.completeAssignment(assignmentId, studentId);

        assertThat(response.getStatus()).isEqualTo(AssignedRoutineStatus.COMPLETED);
        assertThat(response.getCompletedAt()).isNotNull();
    }

    @Test
    void shouldReuseAssignedRoutineAsNewTemplate() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID assignmentId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        AssignedRoutine assignment = assignedRoutine(assignmentId, coachId, studentId);
        assignment.getExercises().getFirst().setExercise(exercise(exerciseId, "Pull Up"));
        ReuseAssignedRoutineRequest request = new ReuseAssignedRoutineRequest();
        request.setReuseMode(ReuseAssignedRoutineRequest.ReuseMode.TEMPLATE);
        request.setName("Reused Pull Day");
        request.setTemplateType(RoutineTemplateType.CUSTOM);

        when(assignedRoutineRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(routineTemplateRepository.save(any(RoutineTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = (com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineTemplateResponse) routinePlanningService.reuseAssignment(coachId, assignmentId, request);

        assertThat(response.getName()).isEqualTo("Reused Pull Day");
        assertThat(response.getExercises()).hasSize(1);
        assertThat(response.getExercises().getFirst().getExerciseName()).isEqualTo("Pull Up");
    }

    private CreateRoutineTemplateRequest templateRequest(UUID exerciseId) {
        CreateRoutineTemplateRequest request = new CreateRoutineTemplateRequest();
        request.setName("PPL Push");
        request.setDescription("Chest, shoulders and triceps");
        request.setTemplateType(RoutineTemplateType.GENERIC);

        CreateRoutineTemplateRequest.TemplateExerciseRequest exercise = new CreateRoutineTemplateRequest.TemplateExerciseRequest();
        exercise.setExerciseId(exerciseId);
        exercise.setSets(4);
        exercise.setReps(10);
        exercise.setRestSeconds(90);
        request.setExercises(List.of(exercise));
        return request;
    }

    private AssignRoutineRequest assignmentRequest(UUID studentId, UUID exerciseId) {
        AssignRoutineRequest request = new AssignRoutineRequest();
        request.setStudentId(studentId);
        request.setName("Week 1 Strength");
        request.setDescription("Heavy compound focus");
        request.setPeriodType(AssignedRoutinePeriodType.WEEK);
        request.setScheduledDate(LocalDate.of(2026, 4, 20));

        AssignRoutineRequest.AssignedExerciseRequest exercise = new AssignRoutineRequest.AssignedExerciseRequest();
        exercise.setExerciseId(exerciseId);
        exercise.setSets(5);
        exercise.setReps(5);
        exercise.setRestSeconds(120);
        request.setExercises(List.of(exercise));
        return request;
    }

    private AssignedRoutine assignedRoutine(UUID assignmentId, UUID coachId, UUID studentId) {
        AssignedRoutine assignment = new AssignedRoutine();
        assignment.setId(assignmentId);
        assignment.setCoach(userWithRole(coachId, Role.RoleName.ROLE_TRAINER));
        assignment.setStudent(userWithRole(studentId, Role.RoleName.ROLE_USER));
        assignment.setName("Assigned Routine");
        assignment.setPeriodType(AssignedRoutinePeriodType.WEEK);
        assignment.setStatus(AssignedRoutineStatus.ASSIGNED);
        assignment.setScheduledDate(LocalDate.of(2026, 4, 20));

        var exercise = new com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutineExercise();
        exercise.setAssignedRoutine(assignment);
        exercise.setSets(4);
        exercise.setReps(8);
        exercise.setRestSeconds(75);
        assignment.setExercises(List.of(exercise));
        return assignment;
    }

    private Exercise exercise(UUID id, String name) {
        Exercise exercise = new Exercise();
        exercise.setId(id);
        exercise.setName(name);
        exercise.setMuscleGroup("back");
        exercise.setDifficulty(Exercise.Difficulty.INTERMEDIATE);
        return exercise;
    }

    private User userWithRole(UUID id, Role.RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);

        User user = new User();
        user.setId(id);
        user.setEmail(id + "@mail.com");
        user.setPasswordHash("encoded");
        user.setFullName("Test User");
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        return user;
    }
}
