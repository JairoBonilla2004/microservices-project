package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateRoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private CreateRoleService createRoleService;

    @Test
    void should_createRole_when_requestIsValid() {
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator role");
        when(roleRepository.existsByNombre("ADMIN")).thenReturn(false);
        Role savedRole = new Role("ADMIN", "Administrator role");
        savedRole.setEstado(ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro.ACTIVO);
        savedRole.markAsPersisted(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), "system", "system");
        when(roleRepository.save(org.mockito.ArgumentMatchers.any(Role.class))).thenReturn(savedRole);

        CreateRoleResponse response = createRoleService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.nombre()).isEqualTo("ADMIN");
        assertThat(response.descripcion()).isEqualTo("Administrator role");
        verify(authorizationPort).requirePermission(Permission.ROLES_CREATE);
        verify(roleRepository).save(org.mockito.ArgumentMatchers.any(Role.class));
    }

    @Test
    void should_throwDuplicateException_when_roleNameAlreadyExists() {
        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator role");
        when(roleRepository.existsByNombre("ADMIN")).thenReturn(true);

        assertThrows(DuplicateException.class, () -> createRoleService.execute(request));
        verify(authorizationPort).requirePermission(Permission.ROLES_CREATE);
    }

    @Test
    void should_throwNullPointerException_when_requestIsNull() {
        assertThrows(NullPointerException.class, () -> createRoleService.execute(null));
    }

    @Test
    void should_throwAuthorizationException_when_missingRolesCreatePermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.ROLES_CREATE);

        CreateRoleRequest request = new CreateRoleRequest("ADMIN", "Administrator role");
        assertThrows(AuthorizationException.class, () -> createRoleService.execute(request));
    }
}
