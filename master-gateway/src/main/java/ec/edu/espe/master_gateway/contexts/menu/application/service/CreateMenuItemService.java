package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.CreateMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.CreateMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de creación de un elemento de menú.
 *
 * <p>Valida los permisos del usuario, construye un nuevo nodo de menú a partir
 * de los datos de la solicitud, lo persiste y retorna la respuesta con los
 * datos del elemento creado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class CreateMenuItemService implements CreateMenuItemUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateMenuItemService.class);

    private final MenuRepositoryPort menuRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public CreateMenuItemService(MenuRepositoryPort menuRepositoryPort,
                                 AuthorizationPort authorizationPort) {
        this.menuRepositoryPort = Objects.requireNonNull(menuRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public MenuItemResponse execute(CreateMenuItemRequest request) {
        authorizationPort.requirePermission(Permission.MENUS_CREATE);

        MenuNode node = new MenuNode(
            request.nombre(),
            request.moduleId(),
            request.parentId(),
            request.orden()
        );

        if (request.url() != null) {
            node.setUrl(request.url());
        }

        MenuNode saved = menuRepositoryPort.save(node);

        log.info("Menu item created: id={}, name={}", saved.getId(), saved.getNombre());

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
