package ec.edu.espe.master_gateway.contexts.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de seguridad para {@link LoginService}.
 *
 * <p>Enfoque Shift-Left: valida el flujo de autenticación de forma aislada
 * (sin contexto de Spring), verificando que los errores de credenciales
 * devuelvan siempre un mensaje genérico (OWASP - no revelar si falla el
 * usuario o la contraseña).</p>
 */
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final String GENERIC_ERROR = "Credenciales inválidas";

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private PasswordHasherPort passwordHasherPort;
    @Mock
    private UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private TokenIssuerPort tokenIssuerPort;

    @InjectMocks
    private LoginService loginService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = mock(User.class);
    }

    @Test
    void should_returnTempTokenAndRoles_when_credentialsAreValid() {
        UUID roleId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("admin", "secret");

        when(userRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(user.isActive()).thenReturn(true);
        when(user.getId()).thenReturn(userId);
        when(user.getPasswordHash()).thenReturn("hashed");
        when(passwordHasherPort.matches("secret", "hashed")).thenReturn(true);

        UserRoleAssignment assignment = mock(UserRoleAssignment.class);
        when(assignment.isActive()).thenReturn(true);
        when(assignment.getRoleId()).thenReturn(roleId);
        when(userRoleAssignmentRepositoryPort.findByUserId(userId)).thenReturn(List.of(assignment));

        Role role = mock(Role.class);
        when(role.getNombre()).thenReturn("ADMIN");
        when(roleRepositoryPort.findById(roleId)).thenReturn(Optional.of(role));

        when(tokenIssuerPort.issueTempToken(userId)).thenReturn("temp-token-123");

        LoginResponse response = loginService.execute(request);

        assertThat(response.tempToken()).isEqualTo("temp-token-123");
        assertThat(response.roles()).hasSize(1);
        assertThat(response.roles().get(0).roleId()).isEqualTo(roleId);
        assertThat(response.roles().get(0).nombre()).isEqualTo("ADMIN");
    }

    @Test
    void should_throwGenericAuthException_when_userNotFound() {
        LoginRequest request = new LoginRequest("ghost", "whatever");
        when(userRepositoryPort.findByUsername("ghost")).thenReturn(Optional.empty());

        AuthenticationException ex =
                assertThrows(AuthenticationException.class, () -> loginService.execute(request));

        assertThat(ex.getMessage()).isEqualTo(GENERIC_ERROR);
        verify(tokenIssuerPort, never()).issueTempToken(any());
    }

    @Test
    void should_throwSameGenericAuthException_when_passwordIsWrong() {
        LoginRequest request = new LoginRequest("admin", "wrong");
        when(userRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(user.isActive()).thenReturn(true);
        when(user.getPasswordHash()).thenReturn("hashed");
        when(passwordHasherPort.matches("wrong", "hashed")).thenReturn(false);

        AuthenticationException ex =
                assertThrows(AuthenticationException.class, () -> loginService.execute(request));

        // Mismo mensaje que el caso "usuario no encontrado": no revela la causa real.
        assertThat(ex.getMessage()).isEqualTo(GENERIC_ERROR);
        verify(tokenIssuerPort, never()).issueTempToken(any());
    }

    @Test
    void should_rejectInactiveUser_withGenericMessage_andNotCheckPassword() {
        LoginRequest request = new LoginRequest("disabled", "secret");
        when(userRepositoryPort.findByUsername("disabled")).thenReturn(Optional.of(user));
        when(user.isActive()).thenReturn(false);

        AuthenticationException ex =
                assertThrows(AuthenticationException.class, () -> loginService.execute(request));

        assertThat(ex.getMessage()).isEqualTo(GENERIC_ERROR);
        // El usuario inactivo se rechaza antes de verificar la contraseña.
        verify(passwordHasherPort, never()).matches(any(), any());
        verify(tokenIssuerPort, never()).issueTempToken(any());
    }
}
