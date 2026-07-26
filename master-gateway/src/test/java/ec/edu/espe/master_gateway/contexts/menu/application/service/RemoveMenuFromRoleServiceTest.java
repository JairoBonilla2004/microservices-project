package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
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
class RemoveMenuFromRoleServiceTest {

    @Mock
    private RoleMenuAssignmentRepositoryPort assignmentRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private RemoveMenuFromRoleService removeMenuFromRoleService;

    private final UUID roleId = UUID.randomUUID();
    private final UUID menuNodeId = UUID.randomUUID();

    @Test
    void should_removeMenuFromRole_when_assignmentExists() {
        RoleMenuAssignment assignment = mock(RoleMenuAssignment.class);
        when(assignmentRepository.findByRoleIdAndMenuNodeId(roleId, menuNodeId))
                .thenReturn(Optional.of(assignment));

        removeMenuFromRoleService.execute(roleId, menuNodeId);

        verify(authorizationPort).requirePermission(Permission.MENUS_ASSIGN);
        verify(assignment).revoke();
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void should_throwNotFoundException_when_assignmentDoesNotExist() {
        when(assignmentRepository.findByRoleIdAndMenuNodeId(roleId, menuNodeId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> removeMenuFromRoleService.execute(roleId, menuNodeId));
        verify(authorizationPort).requirePermission(Permission.MENUS_ASSIGN);
    }

    @Test
    void should_throwNullPointerException_when_roleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> removeMenuFromRoleService.execute(null, menuNodeId));
    }

    @Test
    void should_throwNullPointerException_when_menuNodeIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> removeMenuFromRoleService.execute(roleId, null));
    }

    @Test
    void should_throwAuthorizationException_when_missingMenusAssignPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MENUS_ASSIGN);

        assertThrows(AuthorizationException.class,
                () -> removeMenuFromRoleService.execute(roleId, menuNodeId));
    }
}
