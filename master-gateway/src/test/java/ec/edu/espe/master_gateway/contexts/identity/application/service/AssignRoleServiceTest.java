package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignRoleServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private UserRoleAssignmentRepositoryPort assignmentRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private AssignRoleService service;
    private UUID userId;
    private UUID roleId;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        service = new AssignRoleService(userRepository, roleRepository, assignmentRepository, authorizationPort);
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
    }

    @Test
    void should_assignRole() {
        var request = new AssignRoleRequest(userId, roleId);
        var user = mock(User.class);
        var role = mock(Role.class);
        var assignment = mock(UserRoleAssignment.class);
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(assignmentRepository.findByUserIdAndRoleId(userId, roleId)).thenReturn(Optional.empty());
        when(assignmentRepository.save(any())).thenReturn(assignment);

        service.execute(request);

        assertThat(assignment).isNotNull();
    }

    @Test
    void should_throw_when_userNotFound() {
        var request = new AssignRoleRequest(userId, roleId);
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw_when_roleNotFound() {
        var request = new AssignRoleRequest(userId, roleId);
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(NotFoundException.class);
    }
}
