package com.fitfatlab.fitfatlab_backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "dGVzdC1zZWNyZXQta2V5LWZpdGZhdGxhYi10ZXN0aW5nLTI1NmJpdHMtbG9uZ2Vub3VnaA==");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 60000L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UserDetails user = User.withUsername("user@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractEmail(token)).isEqualTo("user@mail.com");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        UserDetails owner = User.withUsername("owner@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();
        UserDetails stranger = User.withUsername("stranger@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        String token = jwtService.generateToken(owner);

        assertThat(jwtService.isTokenValid(token, stranger)).isFalse();
    }
}
