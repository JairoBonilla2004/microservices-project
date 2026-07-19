package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.AssignModuleToRoleRequest;

/**
 * Caso de uso para asignar un módulo a un rol.
 *
 * <p>Verifica que la asignación no exista previamente antes de
 * crearla para evitar duplicados.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface AssignModuleToRoleUseCase {

    /**
     * Ejecuta la asignación del módulo al rol.
     *
     * @param request datos que contienen el identificador del rol
     *                y del módulo a asignar
     */
    void execute(AssignModuleToRoleRequest request);
}
