package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.MoveMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MoveMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.CycleDetectionPort;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.CycleException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de mover un nodo de menú a un nuevo padre.
 *
 * <p>Valida los permisos del usuario, verifica que el nodo destino exista,
 * detecta posibles ciclos en el árbol y, si todo es correcto, actualiza la
 * relación de parentesco del nodo.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class MoveMenuItemService implements MoveMenuItemUseCase {

    private static final Logger log = LoggerFactory.getLogger(MoveMenuItemService.class);

    private final MenuRepositoryPort menuRepositoryPort;
    private final CycleDetectionPort cycleDetectionPort;
    private final AuthorizationPort authorizationPort;

    public MoveMenuItemService(MenuRepositoryPort menuRepositoryPort,
                                CycleDetectionPort cycleDetectionPort,
                                AuthorizationPort authorizationPort) {
        this.menuRepositoryPort = Objects.requireNonNull(menuRepositoryPort);
        this.cycleDetectionPort = Objects.requireNonNull(cycleDetectionPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(MoveMenuItemRequest request) {
        authorizationPort.requirePermission(Permission.MENUS_UPDATE);

        var node = menuRepositoryPort.findById(request.nodeId())
                .orElseThrow(() -> new NotFoundException("MenuNode", request.nodeId()));

        if (request.newParentId() != null) {
            menuRepositoryPort.findById(request.newParentId())
                    .orElseThrow(() -> new NotFoundException("MenuNode", request.newParentId()));

            if (request.nodeId().equals(request.newParentId())) {
                throw new CycleException("Un nodo no puede ser padre de sí mismo");
            }

            if (cycleDetectionPort.wouldCreateCycle(request.nodeId(), request.newParentId())) {
                throw new CycleException("El movimiento crearía un ciclo en el árbol de menú");
            }
        }

        node.moveTo(request.newParentId());
        menuRepositoryPort.save(node);

        log.info("Menu item moved: nodeId={}, newParentId={}", request.nodeId(), request.newParentId());
    }
}
