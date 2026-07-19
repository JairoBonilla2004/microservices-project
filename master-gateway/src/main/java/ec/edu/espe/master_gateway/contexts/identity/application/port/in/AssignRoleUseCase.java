package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignRoleRequest;

/**
 * Caso de uso para la asignación de un rol a un usuario.
 *
 * <p>Asocia un rol existente a un usuario registrado en el sistema.
 * Valida que tanto el usuario como el rol existan y que el usuario
 * no tenga ya asignado el mismo rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface AssignRoleUseCase {
    /**
     * Ejecuta la asignación de un rol a un usuario.
     *
     * @param request contiene el identificador del usuario y del rol a asignar
     */
    void execute(AssignRoleRequest request);
}
