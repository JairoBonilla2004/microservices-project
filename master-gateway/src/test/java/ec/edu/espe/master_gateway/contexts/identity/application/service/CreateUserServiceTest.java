package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.InvalidInputException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordHasherPort passwordHasher;

    @InjectMocks
    private CreateUserService createUserService;

    private CreateUserRequest request;
    private String passwordHash;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest("jdoe", "jdoe@example.com", "Passw0rd", "John Doe");
        passwordHash = "hashed-password";
    }

    @Test
    void should_createUser_when_requestIsValid() {
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordHasher.hash(request.password())).thenReturn(passwordHash);
        User savedUser = new User(request.username(), request.email(), passwordHash, request.nombreCompleto());
        savedUser.markAsPersisted(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), "system", "system");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        CreateUserResponse response = createUserService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.email()).isEqualTo(request.email());
        assertThat(response.nombreCompleto()).isEqualTo(request.nombreCompleto());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void should_throwDuplicateException_when_usernameAlreadyExists() {
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        DuplicateException ex = assertThrows(DuplicateException.class,
                () -> createUserService.execute(request));

        assertThat(ex.getMessage()).contains("ya existe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_throwDuplicateException_when_emailAlreadyExists() {
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        DuplicateException ex = assertThrows(DuplicateException.class,
                () -> createUserService.execute(request));

        assertThat(ex.getMessage()).contains("ya existe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_throwInvalidInputException_when_usernameContainsHtml() {
        CreateUserRequest htmlRequest = new CreateUserRequest("<script>", "test@example.com", "Passw0rd", "Test");

        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> createUserService.execute(htmlRequest));

        assertThat(ex.getMessage()).contains("username");
        verify(userRepository, never()).save(any(User.class));
    }
}
