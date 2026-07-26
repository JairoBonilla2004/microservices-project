package ec.edu.espe.master_gateway.contexts.module.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
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
class RemoveModuleFromRoleServiceTest {

    @Mock
    private RoleModuleAssignmentRepositoryPort assignmentRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private RemoveModuleFromRoleService removeModuleFromRoleService;

    private final UUID roleId = UUID.randomUUID();
    private final UUID moduleId = UUID.randomUUID();

    @Test
    void should_removeModuleFromRole_when_assignmentExists() {
        RoleModuleAssignment assignment = mock(RoleModuleAssignment.class);
        when(assignmentRepository.findByRoleIdAndModuleId(roleId, moduleId))
                .thenReturn(Optional.of(assignment));

        removeModuleFromRoleService.execute(roleId, moduleId);

        verify(authorizationPort).requirePermission(Permission.MODULES_ASSIGN);
        verify(assignment).revoke();
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void should_throwNotFoundException_when_assignmentDoesNotExist() {
        when(assignmentRepository.findByRoleIdAndModuleId(roleId, moduleId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> removeModuleFromRoleService.execute(roleId, moduleId));
        verify(authorizationPort).requirePermission(Permission.MODULES_ASSIGN);
    }

    @Test
    void should_throwNullPointerException_when_roleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> removeModuleFromRoleService.execute(null, moduleId));
    }

    @Test
    void should_throwNullPointerException_when_moduleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> removeModuleFromRoleService.execute(roleId, null));
    }

    @Test
    void should_throwAuthorizationException_when_missingModulesAssignPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MODULES_ASSIGN);

        assertThrows(AuthorizationException.class,
                () -> removeModuleFromRoleService.execute(roleId, moduleId));
    }
}
