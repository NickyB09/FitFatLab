package com.fitfatlab.fitfatlab_backend.modules.coaching.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CreateCoachStudentLinkRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.UpdateMealEditPermissionRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLink;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoachingServiceImplTest {

    @Mock
    private CoachStudentLinkRepository coachStudentLinkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CoachingServiceImpl coachingService;

    @Test
    void shouldCreatePendingRelationshipForTrainerAndStudent() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);

        CreateCoachStudentLinkRequest request = new CreateCoachStudentLinkRequest();
        request.setStudentId(studentId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(coachStudentLinkRepository.save(any(CoachStudentLink.class)))
                .thenAnswer(invocation -> {
                    CoachStudentLink link = invocation.getArgument(0);
                    link.setId(UUID.randomUUID());
                    return link;
                });

        coachingService.createInvitation(coachId, request);

        ArgumentCaptor<CoachStudentLink> captor = ArgumentCaptor.forClass(CoachStudentLink.class);
        verify(coachStudentLinkRepository).save(captor.capture());
        CoachStudentLink saved = captor.getValue();
        assertThat(saved.getCoach()).isEqualTo(coach);
        assertThat(saved.getStudent()).isEqualTo(student);
        assertThat(saved.getStatus()).isEqualTo(CoachStudentLinkStatus.PENDING);
        assertThat(saved.isAllowStudentMealEdits()).isFalse();
    }

    @Test
    void shouldActivateRelationshipWhenStudentAcceptsAndNoOtherActiveTrainerExists() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID linkId = UUID.randomUUID();
        CoachStudentLink link = pendingLink(linkId, coachId, studentId);

        when(coachStudentLinkRepository.findById(linkId)).thenReturn(Optional.of(link));
        when(coachStudentLinkRepository.existsByStudentIdAndStatus(studentId, CoachStudentLinkStatus.ACTIVE))
                .thenReturn(false);
        when(coachStudentLinkRepository.save(link)).thenReturn(link);

        coachingService.updateStatus(linkId, studentId, CoachStudentLinkStatus.ACTIVE);

        assertThat(link.getStatus()).isEqualTo(CoachStudentLinkStatus.ACTIVE);
        assertThat(link.getActivatedAt()).isNotNull();
    }

    @Test
    void shouldRejectActivationWhenStudentAlreadyHasAnotherActiveTrainer() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID linkId = UUID.randomUUID();
        CoachStudentLink link = pendingLink(linkId, coachId, studentId);

        when(coachStudentLinkRepository.findById(linkId)).thenReturn(Optional.of(link));
        when(coachStudentLinkRepository.existsByStudentIdAndStatus(studentId, CoachStudentLinkStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> coachingService.updateStatus(linkId, studentId, CoachStudentLinkStatus.ACTIVE))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldAllowCoachToEnableMealEditsForActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID linkId = UUID.randomUUID();
        CoachStudentLink link = activeLink(linkId, coachId, studentId);
        UpdateMealEditPermissionRequest request = new UpdateMealEditPermissionRequest();
        request.setAllowStudentMealEdits(true);

        when(coachStudentLinkRepository.findById(linkId)).thenReturn(Optional.of(link));
        when(coachStudentLinkRepository.save(link)).thenReturn(link);

        coachingService.updateMealEditPermission(linkId, coachId, request);

        assertThat(link.isAllowStudentMealEdits()).isTrue();
    }

    @Test
    void shouldRejectMealEditPermissionChangesForNonActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID linkId = UUID.randomUUID();
        CoachStudentLink link = pendingLink(linkId, coachId, studentId);
        UpdateMealEditPermissionRequest request = new UpdateMealEditPermissionRequest();
        request.setAllowStudentMealEdits(true);

        when(coachStudentLinkRepository.findById(linkId)).thenReturn(Optional.of(link));

        assertThatThrownBy(() -> coachingService.updateMealEditPermission(linkId, coachId, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private CoachStudentLink pendingLink(UUID linkId, UUID coachId, UUID studentId) {
        CoachStudentLink link = new CoachStudentLink();
        link.setId(linkId);
        link.setCoach(userWithRole(coachId, Role.RoleName.ROLE_TRAINER));
        link.setStudent(userWithRole(studentId, Role.RoleName.ROLE_USER));
        link.setStatus(CoachStudentLinkStatus.PENDING);
        return link;
    }

    private CoachStudentLink activeLink(UUID linkId, UUID coachId, UUID studentId) {
        CoachStudentLink link = pendingLink(linkId, coachId, studentId);
        link.setStatus(CoachStudentLinkStatus.ACTIVE);
        return link;
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
