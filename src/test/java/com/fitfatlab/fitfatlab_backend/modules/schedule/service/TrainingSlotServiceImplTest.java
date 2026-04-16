package com.fitfatlab.fitfatlab_backend.modules.schedule.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.CreateTrainingSlotRequest;
import com.fitfatlab.fitfatlab_backend.modules.schedule.model.TrainingSlot;
import com.fitfatlab.fitfatlab_backend.modules.schedule.model.TrainingSlotWeekday;
import com.fitfatlab.fitfatlab_backend.modules.schedule.repository.TrainingSlotRepository;
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

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingSlotServiceImplTest {

    @Mock
    private TrainingSlotRepository trainingSlotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoachStudentLinkRepository coachStudentLinkRepository;

    @InjectMocks
    private TrainingSlotServiceImpl trainingSlotService;

    @Test
    void shouldCreateInformativeTrainingSlotForActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);
        CreateTrainingSlotRequest request = slotRequest(studentId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE)).thenReturn(true);
        when(trainingSlotRepository.save(any(TrainingSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = trainingSlotService.createSlot(coachId, request);

        assertThat(response.getStudentId()).isEqualTo(studentId);
        assertThat(response.getWeekday()).isEqualTo(TrainingSlotWeekday.MONDAY);
        assertThat(response.getStartTime()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void shouldRejectSlotCreationWithoutActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);
        CreateTrainingSlotRequest request = slotRequest(studentId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE)).thenReturn(false);

        assertThatThrownBy(() -> trainingSlotService.createSlot(coachId, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldAllowStudentToSeeOwnSlots() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        TrainingSlot slot = new TrainingSlot();
        slot.setId(UUID.randomUUID());
        slot.setCoach(userWithRole(coachId, Role.RoleName.ROLE_TRAINER));
        slot.setStudent(userWithRole(studentId, Role.RoleName.ROLE_USER));
        slot.setWeekday(TrainingSlotWeekday.WEDNESDAY);
        slot.setStartTime(LocalTime.of(7, 30));
        slot.setNote("Morning session");

        when(trainingSlotRepository.findByStudentIdOrderByWeekdayAscStartTimeAsc(studentId)).thenReturn(List.of(slot));

        var response = trainingSlotService.findStudentSlots(studentId);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getNote()).isEqualTo("Morning session");
    }

    private CreateTrainingSlotRequest slotRequest(UUID studentId) {
        CreateTrainingSlotRequest request = new CreateTrainingSlotRequest();
        request.setStudentId(studentId);
        request.setWeekday(TrainingSlotWeekday.MONDAY);
        request.setStartTime(LocalTime.of(18, 0));
        request.setNote("Post-work training");
        return request;
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
