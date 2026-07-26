package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.UpdateMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
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
class UpdateMenuItemServiceTest {

    @Mock
    private MenuRepositoryPort menuRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    private UpdateMenuItemService service;

    private UUID nodeId;
    private UUID moduleId;
    private MenuNode existingNode;

    @BeforeEach
    void setUp() {
        service = new UpdateMenuItemService(menuRepositoryPort, authorizationPort);
        nodeId = UUID.randomUUID();
        moduleId = UUID.randomUUID();
        existingNode = new MenuNode("Old Name", moduleId, null, 1);
        existingNode.setId(nodeId);
        existingNode.setUrl("/old-url");
        existingNode.setEstado(EstadoRegistro.ACTIVO);
        existingNode.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void should_updateAllFields() {
        var request = new UpdateMenuItemRequest("New Name", "/new-url", 5);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(existingNode));
        when(menuRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = service.execute(nodeId, request);

        verify(authorizationPort).requirePermission(Permission.MENUS_UPDATE);
        assertThat(response.nombre()).isEqualTo("New Name");
        assertThat(response.url()).isEqualTo("/new-url");
        assertThat(response.orden()).isEqualTo(5);
    }

    @Test
    void should_updateOnlyNombre() {
        var request = new UpdateMenuItemRequest("New Name", null, null);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(existingNode));
        when(menuRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = service.execute(nodeId, request);

        assertThat(response.nombre()).isEqualTo("New Name");
        assertThat(response.url()).isEqualTo("/old-url");
        assertThat(response.orden()).isEqualTo(1);
    }

    @Test
    void should_updateOnlyUrl() {
        var request = new UpdateMenuItemRequest(null, "/new-url", null);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(existingNode));
        when(menuRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = service.execute(nodeId, request);

        assertThat(response.url()).isEqualTo("/new-url");
        assertThat(response.nombre()).isEqualTo("Old Name");
    }

    @Test
    void should_updateOnlyOrden() {
        var request = new UpdateMenuItemRequest(null, null, 5);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(existingNode));
        when(menuRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = service.execute(nodeId, request);

        assertThat(response.orden()).isEqualTo(5);
    }

    @Test
    void should_throwNotFoundException_when_nodeNotFound() {
        var request = new UpdateMenuItemRequest("Name", null, null);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(nodeId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("MenuNode");
    }

    @Test
    void should_returnMenuItemResponse_withCorrectFields() {
        var request = new UpdateMenuItemRequest("Updated", "/updated", 3);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(existingNode));
        when(menuRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = service.execute(nodeId, request);

        assertThat(response.id()).isEqualTo(nodeId);
        assertThat(response.moduleId()).isEqualTo(moduleId);
        assertThat(response.parentId()).isNull();
        assertThat(response.estado()).isEqualTo("ACTIVO");
    }

    @Test
    void should_keepNullParentId_when_nodeIsRoot() {
        var request = new UpdateMenuItemRequest("Root", null, null);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(existingNode));
        when(menuRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItemResponse response = service.execute(nodeId, request);

        assertThat(response.parentId()).isNull();
    }
}
