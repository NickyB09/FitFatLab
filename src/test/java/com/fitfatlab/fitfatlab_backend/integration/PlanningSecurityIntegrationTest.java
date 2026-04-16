package com.fitfatlab.fitfatlab_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLink;
import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.RoleRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class PlanningSecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CoachStudentLinkRepository coachStudentLinkRepository;

    private MockMvc mockMvc;
    private User trainerOne;
    private User trainerTwo;
    private User student;
    private User plainUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        coachStudentLinkRepository.deleteAll();
        userRepository.deleteAll();

        trainerOne = saveUser("trainer1@mail.com", "Trainer One", Role.RoleName.ROLE_TRAINER);
        trainerTwo = saveUser("trainer2@mail.com", "Trainer Two", Role.RoleName.ROLE_TRAINER);
        student = saveUser("student@mail.com", "Student User", Role.RoleName.ROLE_USER);
        plainUser = saveUser("plain@mail.com", "Plain User", Role.RoleName.ROLE_USER);
    }

    @Test
    @WithMockUser(username = "trainer1@mail.com", roles = {"TRAINER"})
    void trainerShouldCreateCoachStudentInvitation() throws Exception {
        mockMvc.perform(post("/api/v1/coaching/relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("studentId", student.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coachId").value(trainerOne.getId().toString()))
                .andExpect(jsonPath("$.studentId").value(student.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void secondTrainerActivationShouldConflictWhenStudentAlreadyHasActiveTrainer() throws Exception {
        CoachStudentLink activeLink = new CoachStudentLink();
        activeLink.setCoach(trainerOne);
        activeLink.setStudent(student);
        activeLink.setStatus(CoachStudentLinkStatus.ACTIVE);
        coachStudentLinkRepository.save(activeLink);

        CoachStudentLink pendingLink = new CoachStudentLink();
        pendingLink.setCoach(trainerTwo);
        pendingLink.setStudent(student);
        pendingLink.setStatus(CoachStudentLinkStatus.PENDING);
        pendingLink = coachStudentLinkRepository.save(pendingLink);

        mockMvc.perform(patch("/api/v1/coaching/relationships/{id}/status", pendingLink.getId())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("student@mail.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "ACTIVE"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Student already has an active trainer"));
    }

    @Test
    @WithMockUser(username = "plain@mail.com", roles = {"USER"})
    void plainUserShouldNotCreateRoutineTemplate() throws Exception {
        Map<String, Object> body = Map.of(
                "name", "Push Day",
                "description", "Upper body",
                "templateType", "GENERIC",
                "exercises", java.util.List.of(Map.of(
                        "exerciseId", UUID.randomUUID(),
                        "sets", 4,
                        "reps", 10,
                        "restSeconds", 90
                ))
        );

        mockMvc.perform(post("/api/v1/planning/routines/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    private User saveUser(String email, String fullName, Role.RoleName roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("encoded");
        user.setFullName(fullName);
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }
}
