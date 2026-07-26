package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemovePermissionFromRoleServiceTest {

    @Mock
    private RolePermissionAssignmentRepositoryPort assignmentRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private RemovePermissionFromRoleService removePermissionFromRoleService;

    private final UUID roleId = UUID.randomUUID();
    private final Permission permission = Permission.ROLES_CREATE;

    @Test
    void should_removePermission_when_requestIsValid() {
        when(authorizationPort.hasPermission(permission)).thenReturn(true);
        RolePermissionAssignment assignment = mock(RolePermissionAssignment.class);
        when(assignmentRepository.findByRoleIdAndPermission(roleId, permission))
                .thenReturn(Optional.of(assignment));

        removePermissionFromRoleService.execute(roleId, permission);

        verify(authorizationPort).requirePermission(Permission.ROLES_UPDATE);
        verify(assignment).revoke();
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void should_throwAuthorizationException_when_userDoesNotHavePermissionToRemove() {
        when(authorizationPort.hasPermission(permission)).thenReturn(false);

        AuthorizationException ex = assertThrows(AuthorizationException.class,
                () -> removePermissionFromRoleService.execute(roleId, permission));
        assertThat(ex.getMessage()).contains("No puedes remover el permiso");
        verify(authorizationPort).requirePermission(Permission.ROLES_UPDATE);
    }

    @Test
    void should_throwNotFoundException_when_assignmentDoesNotExist() {
        when(authorizationPort.hasPermission(permission)).thenReturn(true);
        when(assignmentRepository.findByRoleIdAndPermission(roleId, permission))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> removePermissionFromRoleService.execute(roleId, permission));
    }

    @Test
    void should_throwNullPointerException_when_roleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> removePermissionFromRoleService.execute(null, permission));
    }

    @Test
    void should_throwNullPointerException_when_permissionIsNull() {
        assertThrows(NullPointerException.class,
                () -> removePermissionFromRoleService.execute(roleId, null));
    }

    @Test
    void should_throwAuthorizationException_when_missingRolesUpdatePermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.ROLES_UPDATE);

        assertThrows(AuthorizationException.class,
                () -> removePermissionFromRoleService.execute(roleId, permission));
    }
}
