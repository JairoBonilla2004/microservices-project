package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para desactivar (dar de baja) un elemento de menú.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface DeactivateMenuItemUseCase {

    /**
     * Desactiva el elemento de menú con el identificador dado.
     *
     * @param id identificador del elemento a desactivar.
     */
    void execute(UUID id);
}
