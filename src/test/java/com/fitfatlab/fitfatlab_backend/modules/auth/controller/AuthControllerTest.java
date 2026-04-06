package com.fitfatlab.fitfatlab_backend.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfatlab.fitfatlab_backend.common.exception.GlobalExceptionHandler;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.AuthResponse;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.LoginRequest;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.RefreshTokenRequest;
import com.fitfatlab.fitfatlab_backend.modules.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService = Mockito.mock(AuthService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void loginShouldReturnOkWhenPayloadIsValid() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("refresh-token")
                .email("user@mail.com")
                .fullName("Fit User")
                .roles(Set.of("ROLE_USER"))
                .expiresIn(3600000L)
                .build();

        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@mail.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.email").value("user@mail.com"));
    }

    @Test
    void loginShouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid-email",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void refreshShouldReturnOkWhenTokenIsValid() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .token("new-access")
                .refreshToken("new-refresh")
                .email("user@mail.com")
                .fullName("Fit User")
                .roles(Set.of("ROLE_USER"))
                .expiresIn(3600000L)
                .build();

        Mockito.when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "old-refresh"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }
}
