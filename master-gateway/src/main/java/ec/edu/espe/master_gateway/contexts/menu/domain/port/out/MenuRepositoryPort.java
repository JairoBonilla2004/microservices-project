package ec.edu.espe.master_gateway.contexts.menu.domain.port.out;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de repositorio para la entidad {@link MenuNode}.
 *
 * <p>Define las operaciones de persistencia para gestionar nodos de
 * menú, permitiendo consultar nodos raíz por módulos, obtener hijos
 * de un nodo padre y persistir cambios en la estructura jerárquica.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface MenuRepositoryPort {

    Optional<MenuNode> findById(UUID id);

    List<MenuNode> findRootNodesByModuleIds(List<UUID> moduleIds);

    List<MenuNode> findChildrenByParentId(UUID parentId);

    MenuNode save(MenuNode menuNode);
}
