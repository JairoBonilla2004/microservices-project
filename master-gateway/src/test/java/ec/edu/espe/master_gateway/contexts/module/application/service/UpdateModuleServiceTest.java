package ec.edu.espe.master_gateway.contexts.module.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.UpdateModuleRequest;
import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
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
class UpdateModuleServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    private UpdateModuleService service;

    private UUID moduleId;
    private Module existingModule;

    @BeforeEach
    void setUp() {
        service = new UpdateModuleService(moduleRepositoryPort, authorizationPort);
        moduleId = UUID.randomUUID();
        existingModule = new Module("Old Name", "Old Desc", "old-icon", 1);
        existingModule.setId(moduleId);
        existingModule.setEstado(EstadoRegistro.ACTIVO);
        existingModule.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void should_updateAllFields() {
        var request = new UpdateModuleRequest("New Name", "New Desc", "new-icon", 5);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModuleResponse response = service.execute(moduleId, request);

        verify(authorizationPort).requirePermission(Permission.MODULES_UPDATE);
        assertThat(response.nombre()).isEqualTo("New Name");
        assertThat(response.descripcion()).isEqualTo("New Desc");
        assertThat(response.icono()).isEqualTo("new-icon");
        assertThat(response.orden()).isEqualTo(5);
    }

    @Test
    void should_updateOnlyNombre() {
        var request = new UpdateModuleRequest("New Name", null, null, null);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModuleResponse response = service.execute(moduleId, request);

        assertThat(response.nombre()).isEqualTo("New Name");
        assertThat(response.descripcion()).isEqualTo("Old Desc");
        assertThat(response.icono()).isEqualTo("old-icon");
        assertThat(response.orden()).isEqualTo(1);
    }

    @Test
    void should_updateOnlyDescripcion() {
        var request = new UpdateModuleRequest(null, "New Desc", null, null);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModuleResponse response = service.execute(moduleId, request);

        assertThat(response.descripcion()).isEqualTo("New Desc");
        assertThat(response.nombre()).isEqualTo("Old Name");
    }

    @Test
    void should_updateOnlyIcono() {
        var request = new UpdateModuleRequest(null, null, "new-icon", null);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModuleResponse response = service.execute(moduleId, request);

        assertThat(response.icono()).isEqualTo("new-icon");
    }

    @Test
    void should_updateOnlyOrden() {
        var request = new UpdateModuleRequest(null, null, null, 5);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModuleResponse response = service.execute(moduleId, request);

        assertThat(response.orden()).isEqualTo(5);
    }

    @Test
    void should_throwNotFoundException_when_moduleNotFound() {
        var request = new UpdateModuleRequest("Name", null, null, null);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(moduleId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Module");
    }

    @Test
    void should_returnModuleResponse_withCorrectFields() {
        var request = new UpdateModuleRequest("Updated", "Updated Desc", "updated-icon", 3);
        when(moduleRepositoryPort.findById(moduleId)).thenReturn(Optional.of(existingModule));
        when(moduleRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ModuleResponse response = service.execute(moduleId, request);

        assertThat(response.id()).isEqualTo(moduleId);
        assertThat(response.estado()).isEqualTo("ACTIVO");
        assertThat(response.fechaCreacion()).isNotNull();
    }
}
