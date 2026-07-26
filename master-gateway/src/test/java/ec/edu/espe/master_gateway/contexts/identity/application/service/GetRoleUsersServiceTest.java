package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
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
class GetRoleUsersServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private UserRoleAssignmentRepositoryPort userRoleAssignmentRepository;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private GetRoleUsersService service;

    private UUID roleId;
    private UUID userId1;
    private UUID userId2;
    private UserRoleAssignment assignment1;
    private UserRoleAssignment assignment2;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        service = new GetRoleUsersService(roleRepository, userRoleAssignmentRepository, userRepository, authorizationPort);
        roleId = UUID.randomUUID();
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        var now = LocalDateTime.now();
        user1 = new User("user1", "user1@test.com", "hash1", "User One");
        user1.markAsPersisted(userId1, now, now, "admin", "admin");
        user1.setEstado(EstadoRegistro.ACTIVO);

        user2 = new User("user2", "user2@test.com", "hash2", "User Two");
        user2.markAsPersisted(userId2, now, now, "admin", "admin");
        user2.setEstado(EstadoRegistro.ACTIVO);

        assignment1 = new UserRoleAssignment(userId1, roleId, "admin");
        assignment2 = new UserRoleAssignment(userId2, roleId, "admin");
    }

    @Test
    void should_returnUsers_when_roleHasAssignments() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(new Role("ROLE_ADMIN", "Admin")));
        when(userRoleAssignmentRepository.findByRoleId(roleId)).thenReturn(List.of(assignment1, assignment2));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));

        List<UserResponse> responses = service.execute(roleId);

        verify(authorizationPort).requirePermission(Permission.ROLES_READ);
        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(UserResponse::username).containsExactly("user1", "user2");
    }

    @Test
    void should_returnEmptyList_when_roleHasNoAssignments() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(new Role("ROLE_ADMIN", "Admin")));
        when(userRoleAssignmentRepository.findByRoleId(roleId)).thenReturn(List.of());

        List<UserResponse> responses = service.execute(roleId);

        assertThat(responses).isEmpty();
    }

    @Test
    void should_throwNotFoundException_when_roleNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(roleId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Rol");
    }

    @Test
    void should_throwNotFoundException_when_userInAssignmentNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(new Role("ROLE_ADMIN", "Admin")));
        when(userRoleAssignmentRepository.findByRoleId(roleId)).thenReturn(List.of(assignment1));
        when(userRepository.findById(userId1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(roleId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void should_throwNullPointerException_when_roleIdIsNull() {
        assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_returnUserResponses_withCorrectFields() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(new Role("ROLE_ADMIN", "Admin")));
        when(userRoleAssignmentRepository.findByRoleId(roleId)).thenReturn(List.of(assignment1));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));

        List<UserResponse> responses = service.execute(roleId);

        UserResponse response = responses.getFirst();
        assertThat(response.id()).isEqualTo(userId1);
        assertThat(response.email()).isEqualTo("user1@test.com");
        assertThat(response.nombreCompleto()).isEqualTo("User One");
        assertThat(response.estado()).isEqualTo("ACTIVO");
        assertThat(response.fechaCreacion()).isNotNull();
        assertThat(response.fechaActualizacion()).isNotNull();
    }
}
