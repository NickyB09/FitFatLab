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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.anyString;

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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "expirationMs", 900000L);
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);
    }

    @Test
    void loginShouldNormalizeEmailAndStoreHashedRefreshToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("  USER@Mail.com ");
        request.setPassword("password123");

        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setFullName("Fit User");
        user.setRoles(Set.of(role));
        user.setId(java.util.UUID.randomUUID());

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("user@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("user@mail.com", request.getPassword()));
        when(userDetailsService.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByEmailIgnoreCase("user@mail.com")).thenReturn(Optional.of(user));
        when(jwtService.hashRefreshToken(anyString())).thenReturn("hashed-stored-refresh");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("user@mail.com");

        ArgumentCaptor<RefreshToken> refreshCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshCaptor.capture());
        RefreshToken savedToken = refreshCaptor.getValue();

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotEqualTo(savedToken.getTokenHash());
        assertThat(savedToken.getTokenHash()).isNotBlank();
        assertThat(savedToken.getTokenHash()).doesNotContain(response.getRefreshToken());
        assertThat(response.getEmail()).isEqualTo("user@mail.com");
        assertThat(response.getExpiresIn()).isEqualTo(900000L);
    }

    @Test
    void loginShouldThrowNotFoundWhenDomainUserDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@mail.com");
        request.setPassword("password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("ghost@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("ghost@mail.com", request.getPassword()));
        when(userDetailsService.loadUserByUsername("ghost@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByEmailIgnoreCase("ghost@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void refreshShouldLookupUsingHashedTokenAndRotateRefreshToken() {
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setFullName("Fit User");
        user.setRoles(Set.of(role));

        String rawRefreshToken = "raw-refresh-token";
        String hashedRefreshToken = "hashed-refresh-token";

        RefreshToken currentToken = new RefreshToken();
        currentToken.setTokenHash(hashedRefreshToken);
        currentToken.setUser(user);
        currentToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("user@mail.com")
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(rawRefreshToken);

        when(jwtService.hashRefreshToken(anyString())).thenAnswer(invocation -> {
            String value = invocation.getArgument(0, String.class);
            return rawRefreshToken.equals(value) ? hashedRefreshToken : "hashed-generated-refresh";
        });
        when(refreshTokenRepository.findByTokenHashAndRevokedFalse(hashedRefreshToken)).thenReturn(Optional.of(currentToken));
        when(userDetailsService.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("rotated-jwt");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.refresh(request);

        assertThat(currentToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).findByTokenHashAndRevokedFalse(hashedRefreshToken);
        ArgumentCaptor<RefreshToken> refreshCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshCaptor.capture());
        assertThat(refreshCaptor.getValue().getTokenHash()).isEqualTo("hashed-generated-refresh");
        assertThat(response.getToken()).isEqualTo("rotated-jwt");
        assertThat(response.getRefreshToken()).isNotBlank();
    }

    @Test
    void logoutShouldRevokeExistingRefreshTokenUsingHashedLookup() {
        RefreshToken currentToken = new RefreshToken();
        currentToken.setTokenHash("hashed-refresh-token");

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(jwtService.hashRefreshToken("refresh-token")).thenReturn("hashed-refresh-token");
        when(refreshTokenRepository.findByTokenHashAndRevokedFalse("hashed-refresh-token")).thenReturn(Optional.of(currentToken));

        authService.logout(request);

        assertThat(currentToken.isRevoked()).isTrue();
    }

    @Test
    void refreshShouldRejectExpiredRefreshToken() {
        User user = new User();
        user.setEmail("user@mail.com");

        RefreshToken expired = new RefreshToken();
        expired.setTokenHash("hashed-refresh-token");
        expired.setUser(user);
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(jwtService.hashRefreshToken("refresh-token")).thenReturn("hashed-refresh-token");
        when(refreshTokenRepository.findByTokenHashAndRevokedFalse("hashed-refresh-token")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

        assertThat(expired.isRevoked()).isTrue();
    }
}
