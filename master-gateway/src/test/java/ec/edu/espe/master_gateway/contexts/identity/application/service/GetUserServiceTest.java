package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private GetUserService getUserService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("jdoe", "jdoe@example.com", "hash", "John Doe");
        user.markAsPersisted(userId, LocalDateTime.now(), LocalDateTime.now(), "system", "system");
    }

    @Test
    void should_returnUserResponse_when_userFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = getUserService.execute(userId);

        verify(authorizationPort).requireOwnershipOrPermission(userId, Permission.USERS_READ);
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.username()).isEqualTo("jdoe");
    }

    @Test
    void should_throwNotFoundException_when_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> getUserService.execute(userId));
    }
}
