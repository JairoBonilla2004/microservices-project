package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MoveMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.CycleDetectionPort;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.CycleException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MoveMenuItemServiceTest {

    @Mock
    private MenuRepositoryPort menuRepositoryPort;
    @Mock
    private CycleDetectionPort cycleDetectionPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private MoveMenuItemService moveMenuItemService;

    private UUID nodeId;
    private UUID newParentId;
    private MoveMenuItemRequest request;

    @BeforeEach
    void setUp() {
        nodeId = UUID.randomUUID();
        newParentId = UUID.randomUUID();
        request = new MoveMenuItemRequest(nodeId, newParentId);
    }

    @Test
    void should_moveNode_when_requestIsValid() {
        MenuNode node = new MenuNode("Child", UUID.randomUUID(), UUID.randomUUID(), 1);
        node.setId(nodeId);
        MenuNode parent = new MenuNode("Parent", UUID.randomUUID(), null, 1);
        parent.setId(newParentId);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(menuRepositoryPort.findById(newParentId)).thenReturn(Optional.of(parent));
        when(cycleDetectionPort.wouldCreateCycle(nodeId, newParentId)).thenReturn(false);

        moveMenuItemService.execute(request);

        verify(authorizationPort).requirePermission(Permission.MENUS_UPDATE);
        verify(menuRepositoryPort).save(node);
    }

    @Test
    void should_throwCycleException_when_cycleDetected() {
        MenuNode node = new MenuNode("Child", UUID.randomUUID(), UUID.randomUUID(), 1);
        node.setId(nodeId);
        MenuNode parent = new MenuNode("Parent", UUID.randomUUID(), null, 1);
        parent.setId(newParentId);
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.of(node));
        when(menuRepositoryPort.findById(newParentId)).thenReturn(Optional.of(parent));
        when(cycleDetectionPort.wouldCreateCycle(nodeId, newParentId)).thenReturn(true);

        assertThrows(CycleException.class, () -> moveMenuItemService.execute(request));
    }

    @Test
    void should_throwNotFoundException_when_nodeDoesNotExist() {
        when(menuRepositoryPort.findById(nodeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> moveMenuItemService.execute(request));
    }
}
