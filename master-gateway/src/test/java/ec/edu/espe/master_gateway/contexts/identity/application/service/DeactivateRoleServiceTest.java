package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeactivateRoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private UserRoleAssignmentRepositoryPort assignmentRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private DeactivateRoleService deactivateRoleService;

    private UUID roleId;
    private Role role;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        role = new Role("ADMIN", "Administrator");
    }

    @Test
    void should_deactivateRole_when_noActiveAssignments() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(assignmentRepository.findByRoleId(roleId)).thenReturn(List.of());

        deactivateRoleService.execute(roleId);

        verify(authorizationPort).requirePermission(Permission.ROLES_DELETE);
        verify(roleRepository).save(role);
    }

    @Test
    void should_throwNotFoundException_when_roleNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deactivateRoleService.execute(roleId));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void should_throwIllegalStateException_when_roleHasActiveUsers() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        UserRoleAssignment assignment = new UserRoleAssignment(UUID.randomUUID(), roleId, "admin");
        when(assignmentRepository.findByRoleId(roleId)).thenReturn(List.of(assignment));

        assertThrows(IllegalStateException.class, () -> deactivateRoleService.execute(roleId));
        verify(roleRepository, never()).save(any());
    }
}
