package com.fitfatlab.fitfatlab_backend.modules.user.controller;

import com.fitfatlab.fitfatlab_backend.common.exception.GlobalExceptionHandler;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserRegistrationRequest;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserResponse;
import com.fitfatlab.fitfatlab_backend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private final UserService userService = Mockito.mock(UserService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void registerShouldReturnCreatedWhenPayloadIsValid() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("new@fitfatlab.com")
                .fullName("New User")
                .enabled(true)
                .roles(Set.of("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userService.register(any(UserRegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new@fitfatlab.com",
                                  "password": "password123",
                                  "fullName": "New User"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@fitfatlab.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void registerShouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": "123",
                                  "fullName": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }
}
