package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.GetAllMenuItemsUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de listado completo de ítems de menú activos.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional(readOnly = true)
public class GetAllMenuItemsService implements GetAllMenuItemsUseCase {

    private final MenuRepositoryPort menuRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public GetAllMenuItemsService(MenuRepositoryPort menuRepositoryPort,
                                  AuthorizationPort authorizationPort) {
        this.menuRepositoryPort = Objects.requireNonNull(menuRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public List<MenuItemResponse> execute() {
        authorizationPort.requirePermission(Permission.MENUS_READ);

        return menuRepositoryPort.findAllActive().stream()
                .map(node -> new MenuItemResponse(
                        node.getId(),
                        node.getNombre(),
                        node.getUrl(),
                        node.getModuleId(),
                        node.getParentId(),
                        node.getOrden(),
                        node.getEstado().name()
                ))
                .toList();
    }
}
