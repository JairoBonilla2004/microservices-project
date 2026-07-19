package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.UpdateMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.UpdateMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de actualización de un elemento de menú.
 *
 * <p>Valida los permisos del usuario, busca el nodo por su identificador, aplica
 * los cambios opcionales proporcionados (nombre, URL, orden) y persiste el nodo
 * actualizado retornando la respuesta con los datos modificados.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class UpdateMenuItemService implements UpdateMenuItemUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateMenuItemService.class);

    private final MenuRepositoryPort menuRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public UpdateMenuItemService(MenuRepositoryPort menuRepositoryPort,
                                 AuthorizationPort authorizationPort) {
        this.menuRepositoryPort = Objects.requireNonNull(menuRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public MenuItemResponse execute(UUID id, UpdateMenuItemRequest request) {
        authorizationPort.requirePermission(Permission.MENUS_UPDATE);

        var node = menuRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("MenuNode", id));

        if (request.nombre() != null) {
            node.setNombre(request.nombre());
        }
        if (request.url() != null) {
            node.setUrl(request.url());
        }
        if (request.orden() != null) {
            node.setOrden(request.orden());
        }

        var saved = menuRepositoryPort.save(node);

        log.info("Menu item updated: id={}, name={}", saved.getId(), saved.getNombre());

        return new MenuItemResponse(
            saved.getId(),
            saved.getNombre(),
            saved.getUrl(),
            saved.getModuleId(),
            saved.getParentId(),
            saved.getOrden(),
            saved.getEstado().name()
        );
    }
}
