package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAllMenuItemsServiceTest {

    @Mock
    private MenuRepositoryPort menuRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;

    @InjectMocks
    private GetAllMenuItemsService getAllMenuItemsService;

    @Test
    void should_returnListOfMenuItems_when_itemsExist() {
        UUID moduleId = UUID.randomUUID();
        MenuNode node = new MenuNode("Dashboard", moduleId, null, 1);
        node.setId(UUID.randomUUID());
        when(menuRepositoryPort.findAllActive()).thenReturn(List.of(node));

        List<MenuItemResponse> result = getAllMenuItemsService.execute();

        assertThat(result).hasSize(1);
        MenuItemResponse response = result.getFirst();
        assertThat(response.nombre()).isEqualTo("Dashboard");
        assertThat(response.moduleId()).isEqualTo(moduleId);
        assertThat(response.parentId()).isNull();
        verify(authorizationPort).requirePermission(Permission.MENUS_READ);
    }

    @Test
    void should_returnEmptyList_when_noMenuItemsExist() {
        when(menuRepositoryPort.findAllActive()).thenReturn(List.of());

        List<MenuItemResponse> result = getAllMenuItemsService.execute();

        assertThat(result).isEmpty();
        verify(authorizationPort).requirePermission(Permission.MENUS_READ);
    }

    @Test
    void should_throwAuthorizationException_when_missingMenusReadPermission() {
        doThrow(new AuthorizationException("No autorizado"))
                .when(authorizationPort).requirePermission(Permission.MENUS_READ);

        assertThrows(AuthorizationException.class,
                () -> getAllMenuItemsService.execute());
    }
}
