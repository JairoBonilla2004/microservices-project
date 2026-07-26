package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class DeactivateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private DeactivateUserService deactivateUserService;

    private UUID userId;
    private UUID currentUserId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
        user = new User("jdoe", "jdoe@example.com", "hash", "John Doe");
        user.markAsPersisted(userId, LocalDateTime.now(), LocalDateTime.now(), "system", "system");
    }

    @Test
    void should_deactivateUser_when_requestIsValid() {
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        deactivateUserService.execute(userId);

        verify(authorizationPort).requirePermission(Permission.USERS_DELETE);
        verify(userRepository).save(user);
    }

    @Test
    void should_throwNotFoundException_when_userNotFound() {
        when(authorizationPort.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deactivateUserService.execute(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void should_throwIllegalArgumentException_when_deactivatingSelf() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);

        assertThrows(IllegalArgumentException.class, () -> deactivateUserService.execute(userId));
        verify(userRepository, never()).save(any());
    }
}
