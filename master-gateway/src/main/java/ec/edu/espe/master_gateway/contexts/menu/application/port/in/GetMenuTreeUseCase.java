package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuNodeResponse;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para obtener el árbol de menú asociado a un rol.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetMenuTreeUseCase {

    /**
     * Recupera la estructura jerárquica (árbol) de menú para el rol indicado.
     *
     * @param roleId identificador del rol.
     * @return lista de nodos raíz con sus hijos anidados recursivamente.
     */
    List<MenuNodeResponse> execute(UUID roleId);
}
