package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserRolesServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private UserRoleAssignmentRepositoryPort userRoleAssignmentRepository;
    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private GetUserRolesService service;

    private UUID userId;
    private UUID roleId1;
    private UUID roleId2;
    private UserRoleAssignment assignment1;
    private UserRoleAssignment assignment2;
    private ec.edu.espe.master_gateway.contexts.identity.domain.model.User user;
    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        service = new GetUserRolesService(userRepository, userRoleAssignmentRepository, roleRepository, authorizationPort);
        userId = UUID.randomUUID();
        roleId1 = UUID.randomUUID();
        roleId2 = UUID.randomUUID();

        var now = LocalDateTime.now();
        user = new ec.edu.espe.master_gateway.contexts.identity.domain.model.User("testuser", "test@test.com", "hash", "Test User");
        user.markAsPersisted(userId, now, now, "admin", "admin");
        user.setEstado(EstadoRegistro.ACTIVO);

        role1 = new Role("ROLE_ADMIN", "Administrator");
        role1.markAsPersisted(roleId1, now, now, "admin", "admin");
        role1.setEstado(EstadoRegistro.ACTIVO);

        role2 = new Role("ROLE_USER", "Regular User");
        role2.markAsPersisted(roleId2, now, now, "admin", "admin");
        role2.setEstado(EstadoRegistro.ACTIVO);

        assignment1 = new UserRoleAssignment(userId, roleId1, "admin");
        assignment2 = new UserRoleAssignment(userId, roleId2, "admin");
    }

    @Test
    void should_returnRoles_when_userHasAssignments() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleAssignmentRepository.findByUserId(userId)).thenReturn(List.of(assignment1, assignment2));
        when(roleRepository.findById(roleId1)).thenReturn(Optional.of(role1));
        when(roleRepository.findById(roleId2)).thenReturn(Optional.of(role2));

        List<RoleResponse> responses = service.execute(userId);

        verify(authorizationPort).requireOwnershipOrPermission(userId, Permission.USERS_READ);
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(RoleResponse::nombre).containsExactly("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void should_returnEmptyList_when_userHasNoAssignments() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleAssignmentRepository.findByUserId(userId)).thenReturn(List.of());

        List<RoleResponse> responses = service.execute(userId);

        assertThat(responses).isEmpty();
    }

    @Test
    void should_throwNotFoundException_when_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void should_throwNotFoundException_when_roleInAssignmentNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleAssignmentRepository.findByUserId(userId)).thenReturn(List.of(assignment1));
        when(roleRepository.findById(roleId1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Rol");
    }

    @Test
    void should_throwNullPointerException_when_userIdIsNull() {
        assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_returnRoleResponses_withCorrectFields() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleAssignmentRepository.findByUserId(userId)).thenReturn(List.of(assignment1));
        when(roleRepository.findById(roleId1)).thenReturn(Optional.of(role1));

        List<RoleResponse> responses = service.execute(userId);

        RoleResponse response = responses.getFirst();
        assertThat(response.id()).isEqualTo(role1.getId());
        assertThat(response.nombre()).isEqualTo("ROLE_ADMIN");
        assertThat(response.descripcion()).isEqualTo("Administrator");
        assertThat(response.estado()).isEqualTo("ACTIVO");
        assertThat(response.fechaCreacion()).isNotNull();
    }
}
