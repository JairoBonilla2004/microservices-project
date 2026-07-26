package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeactivateServiceServiceTest {

    @Mock
    private ServiceRegistryRepositoryPort repository;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private DeactivateServiceService deactivateServiceService;

    private final String serviceCode = "SVC-001";

    @Test
    void should_deactivateService_when_serviceExists() {
        RegisteredService service = new RegisteredService(serviceCode, "Test Service",
                "http://localhost:8080", RegisteredService.ValidationMode.DELEGATE);
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.of(service));

        deactivateServiceService.execute(serviceCode);

        verify(authorizationPort).requirePermission(Permission.SERVICES_DELETE);
        verify(repository).save(service);
    }

    @Test
    void should_throwNotFoundException_when_serviceDoesNotExist() {
        when(repository.findByServiceCode(serviceCode)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> deactivateServiceService.execute(serviceCode));
        verify(authorizationPort).requirePermission(Permission.SERVICES_DELETE);
    }

    @Test
    void should_throwNullPointerException_when_serviceCodeIsNull() {
        assertThrows(NullPointerException.class,
                () -> deactivateServiceService.execute(null));
    }

    @Test
    void should_throwAuthorizationException_when_missingServicesDeletePermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.SERVICES_DELETE);

        assertThrows(AuthorizationException.class,
                () -> deactivateServiceService.execute(serviceCode));
    }
}
