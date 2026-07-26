package ec.edu.espe.master_gateway.contexts.module.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeactivateModuleServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private DeactivateModuleService deactivateModuleService;

    private final UUID moduleId = UUID.randomUUID();

    @Test
    void should_deactivateModule_when_moduleExists() {
        Module module = new Module("Test Module", "Description", "icon-star", 1);
        module.setId(moduleId);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(module));

        deactivateModuleService.execute(moduleId);

        verify(authorizationPort).requirePermission(Permission.MODULES_DELETE);
        verify(moduleRepositoryPort).save(module);
    }

    @Test
    void should_throwNotFoundException_when_moduleDoesNotExist() {
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> deactivateModuleService.execute(moduleId));
        verify(authorizationPort).requirePermission(Permission.MODULES_DELETE);
    }

    @Test
    void should_throwAuthorizationException_when_missingModulesDeletePermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MODULES_DELETE);

        assertThrows(AuthorizationException.class,
                () -> deactivateModuleService.execute(moduleId));
    }
}
