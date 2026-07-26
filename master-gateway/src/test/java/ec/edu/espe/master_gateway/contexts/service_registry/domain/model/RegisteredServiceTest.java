package ec.edu.espe.master_gateway.contexts.service_registry.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class RegisteredServiceTest {

    @Test
    void should_createRegisteredService() {
        var service = new RegisteredService("my-service", "My Service", "http://localhost:8080",
                RegisteredService.ValidationMode.NONE);

        assertThat(service.getServiceCode()).isEqualTo("my-service");
        assertThat(service.getNombre()).isEqualTo("My Service");
        assertThat(service.getBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(service.getValidationMode()).isEqualTo(RegisteredService.ValidationMode.NONE);
    }

    @Test
    void should_deactivateService() {
        var service = new RegisteredService("my-service", "My Service", "http://localhost:8080",
                RegisteredService.ValidationMode.NONE);

        service.deactivate();

        assertThat(service.getEstado()).isEqualTo(ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro.INACTIVO);
    }

    @Test
    void should_updateFields() {
        var service = new RegisteredService("my-service", "My Service", "http://localhost:8080",
                RegisteredService.ValidationMode.NONE);

        service.updateNombre("New Name");
        service.updateBaseUrl("http://new-url:9090");

        assertThat(service.getNombre()).isEqualTo("New Name");
        assertThat(service.getBaseUrl()).isEqualTo("http://new-url:9090");
    }

    @Test
    void should_setAuditFields() {
        var service = new RegisteredService("my-service", "My Service", "http://localhost:8080",
                RegisteredService.ValidationMode.NONE);
        service.setId(java.util.UUID.randomUUID());
        service.setFechaCreacion(LocalDateTime.now());
        service.setFechaActualizacion(LocalDateTime.now());
        service.setCreadoPor("admin");
        service.setActualizadoPor("admin");

        assertThat(service.getId()).isNotNull();
        assertThat(service.getFechaCreacion()).isNotNull();
        assertThat(service.getCreadoPor()).isEqualTo("admin");
    }
}
