package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtIssuerAdapter;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceRequest;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterServiceServiceTest {

    @Mock
    private ServiceRegistryRepositoryPort repository;
    @Mock
    private AuthorizationPort authorizationPort;
    @Mock
    private AsymmetricJwtIssuerAdapter jwtIssuer;

    private RegisterServiceService service;

    @BeforeEach
    void setUp() {
        service = new RegisterServiceService(repository, authorizationPort, jwtIssuer);
    }

    @Test
    void should_registerService_when_codeIsUnique() {
        var request = new RegisterServiceRequest("my-service", "My Service", "http://localhost:8080", "NONE");
        var savedService = new RegisteredService("my-service", "My Service", "http://localhost:8080", RegisteredService.ValidationMode.NONE);
        savedService.setId(UUID.randomUUID());
        savedService.setFechaCreacion(LocalDateTime.now());
        when(repository.existsByServiceCode("my-service")).thenReturn(false);
        when(repository.save(any())).thenReturn(savedService);

        RegisterServiceResponse response = service.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.nombre()).isEqualTo("My Service");
    }

    @Test
    void should_throw_when_serviceCodeAlreadyExists() {
        var request = new RegisterServiceRequest("existing-code", "Existing", "http://localhost:8080", "NONE");
        when(repository.existsByServiceCode("existing-code")).thenReturn(true);

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(DuplicateException.class);
    }
}
