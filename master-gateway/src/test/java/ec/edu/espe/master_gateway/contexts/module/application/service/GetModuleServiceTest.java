package ec.edu.espe.master_gateway.contexts.module.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetModuleServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private GetModuleService getModuleService;

    private final UUID moduleId = UUID.randomUUID();

    @Test
    void should_returnModuleResponse_when_moduleExists() {
        Module module = new Module("Test Module", "Description", "icon-star", 1);
        module.setId(moduleId);
        module.setFechaCreacion(LocalDateTime.now());
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(module));

        ModuleResponse response = getModuleService.execute(moduleId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(moduleId);
        assertThat(response.nombre()).isEqualTo("Test Module");
        assertThat(response.descripcion()).isEqualTo("Description");
        assertThat(response.icono()).isEqualTo("icon-star");
        assertThat(response.orden()).isEqualTo(1);
        verify(authorizationPort).requirePermission(Permission.MODULES_READ);
    }

    @Test
    void should_throwNotFoundException_when_moduleDoesNotExist() {
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> getModuleService.execute(moduleId));
        verify(authorizationPort).requirePermission(Permission.MODULES_READ);
    }

    @Test
    void should_throwAuthorizationException_when_missingModulesReadPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MODULES_READ);

        assertThrows(AuthorizationException.class, () -> getModuleService.execute(moduleId));
    }
}
