package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para eliminar la asignación de un módulo a un rol.
 *
 * <p>Remueve la relación existente entre el rol y el módulo
 * especificados.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RemoveModuleFromRoleUseCase {

    /**
     * Ejecuta la eliminación de la asignación.
     *
     * @param roleId   identificador del rol
     * @param moduleId identificador del módulo
     */
    void execute(UUID roleId, UUID moduleId);
}
