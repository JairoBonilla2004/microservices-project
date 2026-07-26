package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignPermissionToRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de seguridad para {@link AssignPermissionToRoleService}.
 *
 * <p>Verifica el control de acceso (permiso {@code ROLES_UPDATE} y la regla
 * de no poder asignar un permiso que el usuario actual no posee), la
 * prevención de duplicados y el fix de reactivación de asignaciones
 * revocadas para evitar violaciones de la clave única (role_id, permission).</p>
 */
@ExtendWith(MockitoExtension.class)
class AssignPermissionToRoleServiceTest {

    @Mock
    private RolePermissionAssignmentRepositoryPort assignmentRepository;
    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private AssignPermissionToRoleService service;

    private UUID roleId;
    private Permission permission;
    private AssignPermissionToRoleRequest request;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        permission = Permission.MENUS_CREATE;
        request = new AssignPermissionToRoleRequest(permission);
    }

    @Test
    void should_persistNewAssignment_when_permissionIsNotYetAssigned() {
        when(authorizationPort.hasPermission(permission)).thenReturn(true);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mock(Role.class)));
        when(assignmentRepository.findByRoleIdAndPermission(roleId, permission)).thenReturn(Optional.empty());
        when(assignmentRepository.findByRoleIdAndPermissionIncludingInactive(roleId, permission))
                .thenReturn(Optional.empty());

        service.execute(roleId, request);

        verify(authorizationPort).requirePermission(Permission.ROLES_UPDATE);
        ArgumentCaptor<RolePermissionAssignment> captor =
                ArgumentCaptor.forClass(RolePermissionAssignment.class);
        verify(assignmentRepository).save(captor.capture());
        RolePermissionAssignment saved = captor.getValue();
        assertThat(saved.getRoleId()).isEqualTo(roleId);
        assertThat(saved.getPermission()).isEqualTo(permission);
        assertThat(saved.getEstado()).isEqualTo(EstadoRegistro.ACTIVO);
    }

    @Test
    void should_throwDuplicateException_when_permissionAlreadyActive() {
        when(authorizationPort.hasPermission(permission)).thenReturn(true);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mock(Role.class)));
        when(assignmentRepository.findByRoleIdAndPermission(roleId, permission))
                .thenReturn(Optional.of(mock(RolePermissionAssignment.class)));

        assertThrows(DuplicateException.class, () -> service.execute(roleId, request));

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void should_reactivateExistingAssignment_when_permissionWasPreviouslyRevoked() {
        when(authorizationPort.hasPermission(permission)).thenReturn(true);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mock(Role.class)));
        when(assignmentRepository.findByRoleIdAndPermission(roleId, permission)).thenReturn(Optional.empty());

        // Asignación previamente revocada (soft delete -> estado INACTIVO).
        RolePermissionAssignment inactive = new RolePermissionAssignment(roleId, permission, "system");
        inactive.setEstado(EstadoRegistro.INACTIVO);
        when(assignmentRepository.findByRoleIdAndPermissionIncludingInactive(roleId, permission))
                .thenReturn(Optional.of(inactive));

        service.execute(roleId, request);

        // Debe reactivarse la fila existente (no crear una nueva) para evitar duplicate-key.
        ArgumentCaptor<RolePermissionAssignment> captor =
                ArgumentCaptor.forClass(RolePermissionAssignment.class);
        verify(assignmentRepository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(inactive);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoRegistro.ACTIVO);
    }

    @Test
    void should_throwAuthorizationException_when_currentUserDoesNotOwnPermission() {
        when(authorizationPort.hasPermission(permission)).thenReturn(false);

        AuthorizationException ex =
                assertThrows(AuthorizationException.class, () -> service.execute(roleId, request));

        assertThat(ex.getMessage()).contains(permission.name());
        verify(roleRepository, never()).findById(any());
        verify(assignmentRepository, never()).save(any());
    }
}
