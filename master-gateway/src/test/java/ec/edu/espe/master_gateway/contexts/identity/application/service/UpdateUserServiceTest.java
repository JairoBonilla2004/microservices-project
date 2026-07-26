package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.InvalidInputException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordHasherPort passwordHasher;
    @Mock
    private AuthorizationPort authorizationPort;

    private UpdateUserService service;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        service = new UpdateUserService(userRepository, passwordHasher, authorizationPort);
        userId = UUID.randomUUID();
        user = new User("jdoe", "jdoe@example.com", "hashed-password", "John Doe");
        user.markAsPersisted(userId, LocalDateTime.now(), LocalDateTime.now(), "admin", "admin");
    }

    @Test
    void should_updateEmail() {
        var request = new UpdateUserRequest("newemail@example.com", null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var response = service.execute(userId, request);

        assertThat(response.email()).isEqualTo("newemail@example.com");
    }

    @Test
    void should_updateNombreCompleto() {
        var request = new UpdateUserRequest(null, "New Name", null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        var response = service.execute(userId, request);

        assertThat(response.nombreCompleto()).isEqualTo("New Name");
    }

    @Test
    void should_updatePassword_when_currentPasswordIsCorrect() {
        var request = new UpdateUserRequest(null, null, "currentPass", "newPass");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches("currentPass", "hashed-password")).thenReturn(true);
        when(passwordHasher.hash("newPass")).thenReturn("new-hashed");
        when(userRepository.save(any())).thenReturn(user);

        var response = service.execute(userId, request);

        assertThat(response).isNotNull();
    }

    @Test
    void should_throw_when_userNotFound() {
        var request = new UpdateUserRequest(null, null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(userId, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw_when_newPasswordWithoutCurrentPassword() {
        var request = new UpdateUserRequest(null, null, null, "newPass");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.execute(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("contraseña actual");
    }

    @Test
    void should_throw_when_currentPasswordIsWrong() {
        var request = new UpdateUserRequest(null, null, "wrongPass", "newPass");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrongPass", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> service.execute(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("no es correcta");
    }
}
