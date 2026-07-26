package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListRegisteredServicesServiceTest {

    @Mock
    private ServiceRegistryRepositoryPort repository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private ListRegisteredServicesService listRegisteredServicesService;

    @Test
    void should_returnListOfServices_when_servicesExist() {
        RegisteredService service = new RegisteredService("SVC-001", "Test Service",
                "http://localhost:8080", RegisteredService.ValidationMode.DELEGATE);
        service.setId(UUID.randomUUID());
        service.setFechaCreacion(LocalDateTime.now());
        service.setFechaActualizacion(LocalDateTime.now());
        when(repository.findAllActive()).thenReturn(List.of(service));

        List<ServiceResponse> result = listRegisteredServicesService.execute();

        assertThat(result).hasSize(1);
        ServiceResponse response = result.getFirst();
        assertThat(response.serviceCode()).isEqualTo("SVC-001");
        assertThat(response.nombre()).isEqualTo("Test Service");
        assertThat(response.baseUrl()).isEqualTo("http://localhost:8080");
        assertThat(response.validationMode()).isEqualTo("DELEGATE");
        verify(authorizationPort).requirePermission(Permission.SERVICES_READ);
    }

    @Test
    void should_returnEmptyList_when_noServicesExist() {
        when(repository.findAllActive()).thenReturn(List.of());

        List<ServiceResponse> result = listRegisteredServicesService.execute();

        assertThat(result).isEmpty();
        verify(authorizationPort).requirePermission(Permission.SERVICES_READ);
    }

    @Test
    void should_throwAuthorizationException_when_missingServicesReadPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.SERVICES_READ);

        assertThrows(AuthorizationException.class,
                () -> listRegisteredServicesService.execute());
    }
}
