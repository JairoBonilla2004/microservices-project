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

    List<MenuNode> findAllActive();

    List<MenuNode> findRootNodesByModuleIds(List<UUID> moduleIds);

    /**
     * Recupera, en una sola consulta, el árbol de menú completo (nodos raíz por
     * módulo + todos sus descendientes activos), sin incurrir en N+1.
     */
    List<MenuNode> findTreeByModuleIds(List<UUID> moduleIds);

    /**
     * Recupera, en una sola consulta, los nodos indicados junto con todos sus
     * descendientes activos, sin incurrir en N+1.
     */
    List<MenuNode> findSubtreesByNodeIds(List<UUID> nodeIds);

    List<MenuNode> findChildrenByParentId(UUID parentId);

    MenuNode save(MenuNode menuNode);
}
