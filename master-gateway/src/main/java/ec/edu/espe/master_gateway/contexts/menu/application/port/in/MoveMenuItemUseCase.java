package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MoveMenuItemRequest;

/**
 * Caso de uso para mover un nodo de menú a un nuevo padre.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface MoveMenuItemUseCase {

    /**
     * Cambia la relación de parentesco de un nodo de menú.
     *
     * @param request contiene el nodo a mover y el nuevo padre.
     */
    void execute(MoveMenuItemRequest request);
}
