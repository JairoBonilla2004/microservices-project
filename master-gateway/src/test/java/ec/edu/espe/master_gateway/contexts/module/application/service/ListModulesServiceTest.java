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
class ListModulesServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private ListModulesService listModulesService;

    @Test
    void should_returnListOfModules_when_modulesExist() {
        Module module = new Module("Test Module", "Description", "icon-star", 1);
        module.setId(UUID.randomUUID());
        module.setFechaCreacion(LocalDateTime.now());
        when(moduleRepositoryPort.findAll()).thenReturn(List.of(module));

        List<ModuleResponse> result = listModulesService.execute();

        assertThat(result).hasSize(1);
        ModuleResponse response = result.getFirst();
        assertThat(response.nombre()).isEqualTo("Test Module");
        assertThat(response.descripcion()).isEqualTo("Description");
        assertThat(response.icono()).isEqualTo("icon-star");
        verify(authorizationPort).requirePermission(Permission.MODULES_READ);
    }

    @Test
    void should_returnEmptyList_when_noModulesExist() {
        when(moduleRepositoryPort.findAll()).thenReturn(List.of());

        List<ModuleResponse> result = listModulesService.execute();

        assertThat(result).isEmpty();
        verify(authorizationPort).requirePermission(Permission.MODULES_READ);
    }

    @Test
    void should_throwAuthorizationException_when_missingModulesReadPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MODULES_READ);

        assertThrows(AuthorizationException.class,
                () -> listModulesService.execute());
    }
}
