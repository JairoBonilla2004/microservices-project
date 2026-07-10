package ec.edu.espe.master_gateway.contexts.menu.domain.port.out;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import java.util.List;
import java.util.Optional;

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

    Optional<MenuNode> findById(Long id);

    List<MenuNode> findRootNodesByModuleIds(List<Long> moduleIds);

    List<MenuNode> findChildrenByParentId(Long parentId);

    MenuNode save(MenuNode menuNode);
}
