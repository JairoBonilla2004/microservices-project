package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
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
class AdminCreateUserServiceTest {

    @Mock
    private CreateUserUseCase createUserUseCase;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private AdminCreateUserService adminCreateUserService;

    @Test
    void should_createUser_when_adminHasPermission() {
        CreateUserRequest request = new CreateUserRequest("john", "john@test.com", "Password1", "John Doe");
        CreateUserResponse expectedResponse = new CreateUserResponse(
                UUID.randomUUID(), "john", "john@test.com", "John Doe", LocalDateTime.now());
        when(createUserUseCase.execute(request)).thenReturn(expectedResponse);

        CreateUserResponse response = adminCreateUserService.execute(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(authorizationPort).requirePermission(Permission.USERS_CREATE);
        verify(createUserUseCase).execute(request);
    }

    @Test
    void should_throwAuthorizationException_when_missingUsersCreatePermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.USERS_CREATE);

        CreateUserRequest request = new CreateUserRequest("john", "john@test.com", "Password1", "John Doe");
        assertThrows(AuthorizationException.class,
                () -> adminCreateUserService.execute(request));
    }
}
