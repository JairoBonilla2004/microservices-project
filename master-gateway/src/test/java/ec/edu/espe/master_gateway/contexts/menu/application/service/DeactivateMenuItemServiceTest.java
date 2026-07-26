package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
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
class DeactivateMenuItemServiceTest {

    @Mock
    private MenuRepositoryPort menuRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private DeactivateMenuItemService deactivateMenuItemService;

    private final UUID menuNodeId = UUID.randomUUID();

    @Test
    void should_deactivateMenuItem_when_nodeExists() {
        MenuNode node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);
        node.setId(menuNodeId);
        when(menuRepositoryPort.findById(menuNodeId)).thenReturn(Optional.of(node));

        deactivateMenuItemService.execute(menuNodeId);

        verify(authorizationPort).requirePermission(Permission.MENUS_DELETE);
        verify(menuRepositoryPort).save(node);
    }

    @Test
    void should_throwNotFoundException_when_nodeDoesNotExist() {
        when(menuRepositoryPort.findById(menuNodeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> deactivateMenuItemService.execute(menuNodeId));
        verify(authorizationPort).requirePermission(Permission.MENUS_DELETE);
    }

    @Test
    void should_throwAuthorizationException_when_missingMenusDeletePermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MENUS_DELETE);

        assertThrows(AuthorizationException.class,
                () -> deactivateMenuItemService.execute(menuNodeId));
    }
}
