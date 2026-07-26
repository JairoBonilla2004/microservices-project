package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.UpdateServiceRequest;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService.ValidationMode;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateServiceServiceTest {

    @Mock
    private ServiceRegistryRepositoryPort repository;
    @Mock
    private AuthorizationPort authorizationPort;

    private UpdateServiceService service;

    private String serviceCode;
    private RegisteredService existingService;

    @BeforeEach
    void setUp() {
        service = new UpdateServiceService(repository, authorizationPort);
        serviceCode = "my-service";
        existingService = new RegisteredService(serviceCode, "Old Name", "http://old.url", ValidationMode.NONE);
        existingService.setId(UUID.randomUUID());
        existingService.setEstado(EstadoRegistro.ACTIVO);
        existingService.setFechaCreacion(LocalDateTime.now());
        existingService.setFechaActualizacion(LocalDateTime.now());
    }

    @Test
    void should_updateAllFields() {
        var request = new UpdateServiceRequest("New Name", "http://new.url");
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.of(existingService));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = service.execute(serviceCode, request);

        verify(authorizationPort).requirePermission(Permission.SERVICES_UPDATE);
        assertThat(response.nombre()).isEqualTo("New Name");
        assertThat(response.baseUrl()).isEqualTo("http://new.url");
    }

    @Test
    void should_updateOnlyNombre() {
        var request = new UpdateServiceRequest("New Name", null);
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.of(existingService));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = service.execute(serviceCode, request);

        assertThat(response.nombre()).isEqualTo("New Name");
        assertThat(response.baseUrl()).isEqualTo("http://old.url");
    }

    @Test
    void should_updateOnlyBaseUrl() {
        var request = new UpdateServiceRequest(null, "http://new.url");
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.of(existingService));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = service.execute(serviceCode, request);

        assertThat(response.baseUrl()).isEqualTo("http://new.url");
        assertThat(response.nombre()).isEqualTo("Old Name");
    }

    @Test
    void should_throwNotFoundException_when_serviceNotFound() {
        var request = new UpdateServiceRequest("Name", "http://url.com");
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(serviceCode, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("RegisteredService");
    }

    @Test
    void should_throwNullPointerException_when_serviceCodeIsNull() {
        var request = new UpdateServiceRequest("Name", "http://url.com");

        assertThatThrownBy(() -> service.execute(null, request))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_throwNullPointerException_when_requestIsNull() {
        assertThatThrownBy(() -> service.execute(serviceCode, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void should_returnServiceResponse_withCorrectFields() {
        var request = new UpdateServiceRequest("Updated", "http://updated.url");
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.of(existingService));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = service.execute(serviceCode, request);

        assertThat(response.serviceCode()).isEqualTo(serviceCode);
        assertThat(response.estado()).isEqualTo("ACTIVO");
        assertThat(response.validationMode()).isEqualTo("NONE");
        assertThat(response.fechaCreacion()).isNotNull();
        assertThat(response.fechaActualizacion()).isNotNull();
    }
}
