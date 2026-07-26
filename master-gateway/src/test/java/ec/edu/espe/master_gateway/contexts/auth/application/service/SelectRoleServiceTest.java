package ec.edu.espe.master_gateway.contexts.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.PermissionResolverPort;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de seguridad para {@link SelectRoleService}.
 *
 * <p>Verifica el principio de menor privilegio: el access token se emite
 * únicamente con los permisos del rol seleccionado, no con todos los
 * permisos del usuario. También valida la invalidación del token temporal
 * (uso único) y el control de acceso al rol solicitado.</p>
 */
@ExtendWith(MockitoExtension.class)
class SelectRoleServiceTest {

    @Mock
    private TokenValidationPort tokenValidationPort;
    @Mock
    private UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;
    @Mock
    private TokenIssuerPort tokenIssuerPort;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private PermissionResolverPort permissionResolverPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private JwtProperties jwtProperties;

    private SelectRoleService selectRoleService;

    private UUID userId;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        selectRoleService = new SelectRoleService(
                tokenValidationPort,
                userRoleAssignmentRepositoryPort,
                tokenIssuerPort,
                refreshTokenRepositoryPort,
                roleRepositoryPort,
                permissionResolverPort,
                userRepositoryPort,
                jwtProperties);
    }

    @Test
    void should_issueTokensWithOnlyRolePermissions_andInvalidateTempToken() {
        SelectRoleRequest request = new SelectRoleRequest("temp-token", roleId);

        TokenClaims claims = mock(TokenClaims.class);
        when(claims.getUserId()).thenReturn(userId);
        when(tokenValidationPort.validateTempToken("temp-token")).thenReturn(claims);

        UserRoleAssignment assignment = mock(UserRoleAssignment.class);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(assignment));

        Role role = mock(Role.class);
        when(role.getNombre()).thenReturn("EDITOR");
        when(roleRepositoryPort.findById(roleId)).thenReturn(Optional.of(role));

        // El rol EDITOR sólo tiene estos dos permisos: el token NO debe contener otros.
        Set<Permission> rolePermissions = Set.of(Permission.MENUS_READ, Permission.MENUS_UPDATE);
        when(permissionResolverPort.resolvePermissions("EDITOR")).thenReturn(rolePermissions);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("editor.user");
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));

        when(tokenIssuerPort.issueAccessToken(any(), any(), any(), any(), any()))
                .thenReturn("access-token");
        when(tokenIssuerPort.issueRefreshToken(userId, roleId, "EDITOR"))
                .thenReturn("refresh-token");

        SelectRoleResponse response = selectRoleService.execute(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");

        Set<String> expectedPermissions = Set.of("MENUS_READ", "MENUS_UPDATE");
        verify(tokenIssuerPort).issueAccessToken(
                userId, roleId, expectedPermissions, "EDITOR", "editor.user");

        // El refresh token se persiste y el temp token de un solo uso se invalida.
        verify(refreshTokenRepositoryPort).save(any(RefreshToken.class));
        verify(tokenValidationPort).invalidateTempToken("temp-token");
    }

    @Test
    void should_throwAuthenticationException_when_tempTokenIsInvalidOrExpired() {
        SelectRoleRequest request = new SelectRoleRequest("bad-token", roleId);
        when(tokenValidationPort.validateTempToken("bad-token"))
                .thenThrow(new JwtException("expired"));

        AuthenticationException ex =
                assertThrows(AuthenticationException.class, () -> selectRoleService.execute(request));

        assertThat(ex.getMessage()).isEqualTo("Token temporal inválido o expirado");
        verify(tokenIssuerPort, never()).issueAccessToken(any(), any(), any(), any(), any());
        verify(tokenValidationPort, never()).invalidateTempToken(any());
    }

    @Test
    void should_throwAuthorizationException_when_userDoesNotHaveRequestedRole() {
        SelectRoleRequest request = new SelectRoleRequest("temp-token", roleId);

        TokenClaims claims = mock(TokenClaims.class);
        when(claims.getUserId()).thenReturn(userId);
        when(tokenValidationPort.validateTempToken("temp-token")).thenReturn(claims);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());

        AuthorizationException ex =
                assertThrows(AuthorizationException.class, () -> selectRoleService.execute(request));

        assertThat(ex.getMessage()).isEqualTo("El usuario no tiene el rol solicitado");
        verify(tokenIssuerPort, never()).issueAccessToken(any(), any(), any(), any(), any());
        // No se invalida el temp token si la autorización falla.
        verify(tokenValidationPort, never()).invalidateTempToken(any());
    }
}
