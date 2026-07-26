package ec.edu.espe.master_gateway.contexts.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.PermissionResolverPort;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock
    private TokenIssuerPort tokenIssuerPort;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private PermissionResolverPort permissionResolverPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UUID userId;
    private UUID roleId;
    private String refreshTokenValue;
    private RefreshToken existingToken;
    private RefreshTokenRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        refreshTokenValue = "refresh-token-value";
        request = new RefreshTokenRequest(refreshTokenValue);
        existingToken = new RefreshToken(refreshTokenValue, userId, roleId, LocalDateTime.now().plusDays(7));
    }

    @Test
    void should_refreshTokens_when_tokenIsValid() {
        lenient().when(jwtProperties.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        when(refreshTokenRepositoryPort.findByToken(refreshTokenValue))
                .thenReturn(Optional.of(existingToken));
        Role role = new Role("ADMIN", "Administrator");
        when(roleRepositoryPort.findById(existingToken.getRoleId()))
                .thenReturn(Optional.of(role));
        when(permissionResolverPort.resolvePermissions(role.getNombre()))
                .thenReturn(Set.of(Permission.USERS_READ, Permission.USERS_CREATE));
        User user = new User("jdoe", "jdoe@example.com", "hash", "John Doe");
        user.markAsPersisted(userId, LocalDateTime.now(), LocalDateTime.now(), "system", "system");
        when(userRepositoryPort.findById(existingToken.getUserId()))
                .thenReturn(Optional.of(user));
        when(tokenIssuerPort.issueAccessToken(any(), any(), any(), any(), any()))
                .thenReturn("new-access-token");
        when(tokenIssuerPort.issueRefreshToken(any(), any(), any()))
                .thenReturn("new-refresh-token");

        RefreshTokenResponse response = refreshTokenService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenRepositoryPort, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void should_throwAuthenticationException_when_tokenIsExpired() {
        RefreshToken expiredToken = new RefreshToken(refreshTokenValue, userId, roleId, LocalDateTime.now().minusDays(1));
        when(refreshTokenRepositoryPort.findByToken(refreshTokenValue))
                .thenReturn(Optional.of(expiredToken));

        assertThrows(AuthenticationException.class, () -> refreshTokenService.execute(request));
    }

    @Test
    void should_throwAuthenticationException_when_tokenNotFound() {
        when(refreshTokenRepositoryPort.findByToken(refreshTokenValue))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> refreshTokenService.execute(request));
    }
}
