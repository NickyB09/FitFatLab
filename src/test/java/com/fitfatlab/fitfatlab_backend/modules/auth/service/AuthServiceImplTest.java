package com.fitfatlab.fitfatlab_backend.modules.auth.service;

import com.fitfatlab.fitfatlab_backend.modules.auth.dto.AuthResponse;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.LoginRequest;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.RefreshTokenRequest;
import com.fitfatlab.fitfatlab_backend.modules.auth.model.RefreshToken;
import com.fitfatlab.fitfatlab_backend.modules.auth.repository.RefreshTokenRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import com.fitfatlab.fitfatlab_backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginShouldAuthenticateAndReturnTokenPayload() {
        ReflectionTestUtils.setField(authService, "expirationMs", 3600000L);
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 86400000L);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@mail.com");
        request.setPassword("password123");

        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setFullName("Fit User");
        user.setRoles(Set.of(role));
        user.setId(java.util.UUID.randomUUID());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("user@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        when(userDetailsService.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getEmail()).isEqualTo("user@mail.com");
        assertThat(response.getRoles()).containsExactly("ROLE_USER");
        assertThat(response.getExpiresIn()).isEqualTo(3600000L);
    }

    @Test
    void loginShouldThrowNotFoundWhenDomainUserDoesNotExist() {
        ReflectionTestUtils.setField(authService, "expirationMs", 3600000L);
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 86400000L);

        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@mail.com");
        request.setPassword("password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("ghost@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        when(userDetailsService.loadUserByUsername("ghost@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void refreshShouldRotateRefreshTokenAndReturnNewPair() {
        ReflectionTestUtils.setField(authService, "expirationMs", 3600000L);
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 86400000L);

        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setFullName("Fit User");
        user.setRoles(Set.of(role));

        RefreshToken currentToken = new RefreshToken();
        currentToken.setToken("old-refresh");
        currentToken.setUser(user);
        currentToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        RefreshToken newToken = new RefreshToken();
        newToken.setToken("new-refresh");
        newToken.setUser(user);
        newToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("user@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("old-refresh")).thenReturn(Optional.of(currentToken));
        when(userDetailsService.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("rotated-jwt");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(newToken);

        AuthResponse response = authService.refresh(request);

        assertThat(currentToken.isRevoked()).isTrue();
        assertThat(response.getToken()).isEqualTo("rotated-jwt");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void logoutShouldRevokeExistingRefreshToken() {
        RefreshToken currentToken = new RefreshToken();
        currentToken.setToken("refresh-token");

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("refresh-token")).thenReturn(Optional.of(currentToken));

        authService.logout(request);

        assertThat(currentToken.isRevoked()).isTrue();
    }
}
