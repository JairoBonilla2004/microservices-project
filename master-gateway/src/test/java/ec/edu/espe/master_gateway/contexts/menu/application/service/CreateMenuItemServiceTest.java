package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.CreateMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateMenuItemServiceTest {

    @Mock
    private MenuRepositoryPort menuRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private CreateMenuItemService service;

    @BeforeEach
    void setUp() {
        service = new CreateMenuItemService(menuRepository, authorizationPort);
    }

    @Test
    void should_createRootMenuItem() {
        var moduleId = UUID.randomUUID();
        var request = new CreateMenuItemRequest("Dashboard", "/dashboard", moduleId, null, 1);
        var node = new MenuNode("Dashboard", moduleId, null, 1);
        node.setUrl("/dashboard");
        node.setId(UUID.randomUUID());
        when(menuRepository.save(any())).thenReturn(node);

        MenuItemResponse response = service.execute(request);

        assertThat(response.nombre()).isEqualTo("Dashboard");
    }

    @Test
    void should_createChildMenuItem() {
        var parentId = UUID.randomUUID();
        var moduleId = UUID.randomUUID();
        var request = new CreateMenuItemRequest("Child", "/child", moduleId, parentId, 2);
        var child = new MenuNode("Child", moduleId, parentId, 2);
        child.setUrl("/child");
        child.setId(UUID.randomUUID());
        when(menuRepository.save(any())).thenReturn(child);

        MenuItemResponse response = service.execute(request);

        assertThat(response.nombre()).isEqualTo("Child");
    }
}
