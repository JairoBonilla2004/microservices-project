package ec.edu.espe.master_gateway.contexts.module.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.AssignModuleToRoleRequest;
import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
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
class AssignModuleToRoleServiceTest {

    @Mock
    private RoleModuleAssignmentRepositoryPort roleModuleAssignmentRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private AssignModuleToRoleService assignModuleToRoleService;

    private final UUID roleId = UUID.randomUUID();
    private final UUID moduleId = UUID.randomUUID();

    @Test
    void should_assignModuleToRole_when_requestIsValid() {
        AssignModuleToRoleRequest request = new AssignModuleToRoleRequest(roleId, moduleId);
        when(roleModuleAssignmentRepositoryPort.findByRoleIdAndModuleId(roleId, moduleId))
                .thenReturn(Optional.empty());

        assignModuleToRoleService.execute(request);

        verify(authorizationPort).requirePermission(Permission.MODULES_ASSIGN);
        verify(roleModuleAssignmentRepositoryPort)
                .save(org.mockito.ArgumentMatchers.any(RoleModuleAssignment.class));
    }

    @Test
    void should_throwDuplicateException_when_assignmentAlreadyExists() {
        AssignModuleToRoleRequest request = new AssignModuleToRoleRequest(roleId, moduleId);
        when(roleModuleAssignmentRepositoryPort.findByRoleIdAndModuleId(roleId, moduleId))
                .thenReturn(Optional.of(mock(RoleModuleAssignment.class)));

        assertThrows(DuplicateException.class,
                () -> assignModuleToRoleService.execute(request));
        verify(authorizationPort).requirePermission(Permission.MODULES_ASSIGN);
    }

    @Test
    void should_throwAuthorizationException_when_missingModulesAssignPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MODULES_ASSIGN);

        AssignModuleToRoleRequest request = new AssignModuleToRoleRequest(roleId, moduleId);
        assertThrows(AuthorizationException.class,
                () -> assignModuleToRoleService.execute(request));
    }
}
