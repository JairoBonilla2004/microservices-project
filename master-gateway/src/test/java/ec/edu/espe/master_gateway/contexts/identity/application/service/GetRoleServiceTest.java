package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
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

@ExtendWith(MockitoExtension.class)
class GetRoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private GetRoleService getRoleService;

    private UUID roleId;
    private Role role;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        role = new Role("ADMIN", "Administrator");
    }

    @Test
    void should_returnRoleResponse_when_roleFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        RoleResponse response = getRoleService.execute(roleId);

        verify(authorizationPort).requirePermission(Permission.ROLES_READ);
        assertThat(response).isNotNull();
        assertThat(response.nombre()).isEqualTo("ADMIN");
        assertThat(response.descripcion()).isEqualTo("Administrator");
    }

    @Test
    void should_throwNotFoundException_when_roleNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> getRoleService.execute(roleId));
    }
}
