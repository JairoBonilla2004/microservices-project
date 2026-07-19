package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.AssignMenuToRoleRequest;

/**
 * Caso de uso para asignar un nodo de menú a un rol.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface AssignMenuToRoleUseCase {

    /**
     * Asigna el nodo de menú especificado al rol indicado.
     *
     * @param request contiene el identificador del rol y del nodo de menú.
     */
    void execute(AssignMenuToRoleRequest request);
}
