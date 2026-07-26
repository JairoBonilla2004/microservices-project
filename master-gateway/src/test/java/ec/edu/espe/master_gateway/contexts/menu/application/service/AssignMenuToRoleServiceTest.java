package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.AssignMenuToRoleRequest;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
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
class AssignMenuToRoleServiceTest {

    @Mock
    private RoleMenuAssignmentRepositoryPort roleMenuAssignmentRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private AssignMenuToRoleService assignMenuToRoleService;

    private final UUID roleId = UUID.randomUUID();
    private final UUID menuNodeId = UUID.randomUUID();

    @Test
    void should_assignMenuToRole_when_requestIsValid() {
        AssignMenuToRoleRequest request = new AssignMenuToRoleRequest(roleId, menuNodeId);
        when(roleMenuAssignmentRepositoryPort.findByRoleIdAndMenuNodeId(roleId, menuNodeId))
                .thenReturn(Optional.empty());

        assignMenuToRoleService.execute(request);

        verify(authorizationPort).requirePermission(Permission.MENUS_ASSIGN);
        verify(roleMenuAssignmentRepositoryPort)
                .save(org.mockito.ArgumentMatchers.any(RoleMenuAssignment.class));
    }

    @Test
    void should_throwDuplicateException_when_assignmentAlreadyExists() {
        AssignMenuToRoleRequest request = new AssignMenuToRoleRequest(roleId, menuNodeId);
        when(roleMenuAssignmentRepositoryPort.findByRoleIdAndMenuNodeId(roleId, menuNodeId))
                .thenReturn(Optional.of(mock(RoleMenuAssignment.class)));

        assertThrows(DuplicateException.class,
                () -> assignMenuToRoleService.execute(request));
        verify(authorizationPort).requirePermission(Permission.MENUS_ASSIGN);
    }

    @Test
    void should_throwAuthorizationException_when_missingMenusAssignPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MENUS_ASSIGN);

        AssignMenuToRoleRequest request = new AssignMenuToRoleRequest(roleId, menuNodeId);
        assertThrows(AuthorizationException.class,
                () -> assignMenuToRoleService.execute(request));
    }
}
