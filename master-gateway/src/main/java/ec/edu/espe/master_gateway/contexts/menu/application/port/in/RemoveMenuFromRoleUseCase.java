package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para eliminar la asignación de un nodo de menú a un rol.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RemoveMenuFromRoleUseCase {

    /**
     * Remueve la asignación entre el rol y el nodo de menú especificados.
     *
     * @param roleId     identificador del rol.
     * @param menuNodeId identificador del nodo de menú.
     */
    void execute(UUID roleId, UUID menuNodeId);
}
