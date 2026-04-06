package com.fitfatlab.fitfatlab_backend.modules.user.service;

import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserRegistrationRequest;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserResponse;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserUpdateRequest;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.RoleRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerShouldEncodePasswordAssignRoleAndNormalizeFields() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("  USER@Mail.com ");
        request.setPassword("supersecret");
        request.setFullName("  Nicolas Betancur ");

        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail("user@mail.com");
        savedUser.setFullName("Nicolas Betancur");
        savedUser.setEnabled(true);
        savedUser.setRoles(Set.of(role));
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("supersecret")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User persisted = userCaptor.getValue();

        assertThat(persisted.getEmail()).isEqualTo("user@mail.com");
        assertThat(persisted.getFullName()).isEqualTo("Nicolas Betancur");
        assertThat(persisted.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(persisted.getRoles()).containsExactly(role);

        assertThat(response.getEmail()).isEqualTo("user@mail.com");
        assertThat(response.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void registerShouldThrowConflictWhenEmailAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("existing@mail.com");

        when(userRepository.existsByEmail("existing@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException error = (ResponseStatusException) ex;
                    assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });
    }

    @Test
    void findByIdShouldReturnMappedUser() {
        UUID userId = UUID.randomUUID();
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_ADMIN);

        User user = new User();
        user.setId(userId);
        user.setEmail("admin@fitfatlab.com");
        user.setFullName("Admin User");
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.findById(userId);

        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("admin@fitfatlab.com");
        assertThat(response.getRoles()).containsExactly("ROLE_ADMIN");
    }

    @Test
    void updateProfileShouldUpdateProvidedFields() {
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Updated Name");
        request.setPassword("newpassword");

        User user = new User();
        user.setId(userId);
        user.setEmail("user@mail.com");
        user.setFullName("Old Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("encoded-new-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateProfile(userId, request);

        assertThat(user.getFullName()).isEqualTo("Updated Name");
        assertThat(user.getPasswordHash()).isEqualTo("encoded-new-password");
        assertThat(response.getFullName()).isEqualTo("Updated Name");
    }
}
