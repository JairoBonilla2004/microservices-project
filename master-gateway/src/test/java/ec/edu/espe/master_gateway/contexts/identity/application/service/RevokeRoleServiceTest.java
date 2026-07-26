package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de seguridad para {@link RevokeRoleService}.
 *
 * <p>Verifica el control de acceso (permiso {@code ROLES_ASSIGN_USERS}),
 * la protección contra auto-revocación y que la revocación se realice
 * mediante borrado físico ({@code hardDeleteByUserIdAndRoleId}), no soft
 * delete.</p>
 */
@ExtendWith(MockitoExtension.class)
class RevokeRoleServiceTest {

    @Mock
    private UserRoleAssignmentRepositoryPort assignmentRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private RevokeRoleService revokeRoleService;

    private UUID targetUserId;
    private UUID roleId;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        targetUserId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
    }

    @Test
    void should_hardDeleteAssignment_when_revocationIsValid() {
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        UserRoleAssignment assignment = mock(UserRoleAssignment.class);
        when(assignmentRepository.findByUserIdAndRoleId(targetUserId, roleId))
                .thenReturn(Optional.of(assignment));

        revokeRoleService.execute(targetUserId, roleId);

        verify(authorizationPort).requirePermission(Permission.ROLES_ASSIGN_USERS);
        // Borrado físico: no debe usarse save() ni el soft-delete revoke().
        verify(assignmentRepository).hardDeleteByUserIdAndRoleId(targetUserId, roleId);
        verify(assignmentRepository, never()).save(any());
        verify(assignment, never()).revoke();
    }

    @Test
    void should_throwIllegalArgumentException_when_userRevokesOwnRole() {
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> revokeRoleService.execute(currentUserId, roleId));

        assertThat(ex.getMessage()).isEqualTo("No puedes revocarte tu propio rol");
        verify(assignmentRepository, never()).hardDeleteByUserIdAndRoleId(any(), any());
    }

    @Test
    void should_throwNotFoundException_when_assignmentDoesNotExist() {
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        when(assignmentRepository.findByUserIdAndRoleId(targetUserId, roleId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> revokeRoleService.execute(targetUserId, roleId));

        verify(assignmentRepository, never()).hardDeleteByUserIdAndRoleId(any(), any());
    }

    @Test
    void should_throwAuthorizationException_when_missingRolesAssignUsersPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.ROLES_ASSIGN_USERS);

        assertThrows(AuthorizationException.class,
                () -> revokeRoleService.execute(targetUserId, roleId));

        verify(assignmentRepository, never()).findByUserIdAndRoleId(any(), any());
        verify(assignmentRepository, never()).hardDeleteByUserIdAndRoleId(any(), any());
    }
}
