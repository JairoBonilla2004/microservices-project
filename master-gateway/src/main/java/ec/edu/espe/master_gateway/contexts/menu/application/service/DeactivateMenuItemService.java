package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.DeactivateMenuItemUseCase;
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
 * Servicio que implementa el caso de uso de desactivación de un elemento de menú.
 *
 * <p>Valida los permisos del usuario, busca el nodo por su identificador,
 * lo marca como inactivo y persiste los cambios en el repositorio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class DeactivateMenuItemService implements DeactivateMenuItemUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeactivateMenuItemService.class);

    private final MenuRepositoryPort menuRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public DeactivateMenuItemService(MenuRepositoryPort menuRepositoryPort,
                                     AuthorizationPort authorizationPort) {
        this.menuRepositoryPort = Objects.requireNonNull(menuRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID id) {
        authorizationPort.requirePermission(Permission.MENUS_DELETE);

        var node = menuRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("MenuNode", id));
        node.deactivate();
        menuRepositoryPort.save(node);

        log.info("Menu item deactivated: id={}, name={}", id, node.getNombre());
    }
}
