package ec.edu.espe.master_gateway.contexts.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;
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
class RegisterUserServiceTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private RegisterUserService registerUserService;

    private RegisterUserRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterUserRequest("jdoe", "jdoe@example.com", "Passw0rd", "Passw0rd", "John Doe");
    }

    @Test
    void should_registerUser_when_requestIsValid() {
        UUID userId = UUID.randomUUID();
        CreateUserResponse createdResponse = new CreateUserResponse(
                userId, request.username(), request.email(), request.nombreCompleto(), LocalDateTime.now());
        when(createUserUseCase.execute(any(CreateUserRequest.class))).thenReturn(createdResponse);

        RegisterUserResponse response = registerUserService.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.username()).isEqualTo(request.username());
    }

    @Test
    void should_throwInvalidInputException_when_passwordsDoNotMatch() {
        RegisterUserRequest mismatchRequest = new RegisterUserRequest(
                "jdoe", "jdoe@example.com", "Passw0rd", "DifferentPass1", "John Doe");

        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> registerUserService.execute(mismatchRequest));

        assertThat(ex.getMessage()).contains("contraseñas no coinciden");
    }

    @Test
    void should_propagateDuplicateException_when_usernameAlreadyExists() {
        when(createUserUseCase.execute(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateException("Usuario", "username", request.username()));

        assertThrows(DuplicateException.class, () -> registerUserService.execute(request));
    }
}
