package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateRoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private UpdateRoleService service;

    private UUID roleId;
    private Role existingRole;

    @BeforeEach
    void setUp() {
        service = new UpdateRoleService(roleRepository, authorizationPort);
        roleId = UUID.randomUUID();
        existingRole = new Role("OLD_ROLE", "Old description");
        existingRole.markAsPersisted(roleId, LocalDateTime.now(), LocalDateTime.now(), "admin", "admin");
        existingRole.setEstado(EstadoRegistro.ACTIVO);
    }

    @Test
    void should_updateBothFields() {
        var request = new UpdateRoleRequest("NEW_ROLE", "New description");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = service.execute(roleId, request);

        verify(authorizationPort).requirePermission(Permission.ROLES_UPDATE);
        assertThat(response.nombre()).isEqualTo("NEW_ROLE");
        assertThat(response.descripcion()).isEqualTo("New description");
    }

    @Test
    void should_updateOnlyNombre() {
        var request = new UpdateRoleRequest("NEW_ROLE", null);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = service.execute(roleId, request);

        assertThat(response.nombre()).isEqualTo("NEW_ROLE");
        assertThat(response.descripcion()).isEqualTo("Old description");
    }

    @Test
    void should_updateOnlyDescripcion() {
        var request = new UpdateRoleRequest(null, "New description");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = service.execute(roleId, request);

        assertThat(response.descripcion()).isEqualTo("New description");
        assertThat(response.nombre()).isEqualTo("OLD_ROLE");
    }

    @Test
    void should_throwNotFoundException_when_roleNotFound() {
        var request = new UpdateRoleRequest("NAME", "desc");
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(roleId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Rol");
    }

    @Test
    void should_throwNullPointerException_when_idIsNull() {
        var request = new UpdateRoleRequest("NAME", "desc");

        assertThatThrownBy(() -> service.execute(null, request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throwNullPointerException_when_requestIsNull() {
        assertThatThrownBy(() -> service.execute(roleId, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_returnRoleResponse_withCorrectFields() {
        var request = new UpdateRoleRequest("UPDATED", "Updated desc");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = service.execute(roleId, request);

        assertThat(response.id()).isEqualTo(roleId);
        assertThat(response.estado()).isEqualTo("ACTIVO");
        assertThat(response.fechaCreacion()).isNotNull();
    }
}
