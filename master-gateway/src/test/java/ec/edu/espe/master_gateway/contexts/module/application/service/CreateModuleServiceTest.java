package ec.edu.espe.master_gateway.contexts.module.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleRequest;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
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
class CreateModuleServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private CreateModuleService service;

    @BeforeEach
    void setUp() {
        service = new CreateModuleService(moduleRepository, authorizationPort);
    }

    @Test
    void should_createModule() {
        var request = new CreateModuleRequest("User Management", "Gestión de usuarios", "/users", 1);
        var module = new Module("User Management", "Gestión de usuarios", "/users", 1);
        module.setId(UUID.randomUUID());
        module.setFechaCreacion(LocalDateTime.now());
        when(moduleRepository.save(any())).thenReturn(module);

        CreateModuleResponse response = service.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.nombre()).isEqualTo("User Management");
    }

    @Test
    void should_throw_when_moduleNameAlreadyExists() {
        var request = new CreateModuleRequest("Existing", "Desc", "/path", 2);
        when(moduleRepository.existsByNombre("Existing")).thenReturn(true);

        assertThrows(DuplicateException.class, () -> service.execute(request));
    }
}
