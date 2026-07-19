package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para la desactivación de un rol en el sistema.
 *
 * <p>Marca un rol como inactivo, impidiendo que pueda ser asignado a
 * nuevos usuarios. Los usuarios que ya poseen el rol conservan sus
 * permisos hasta que el rol sea removido explícitamente.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface DeactivateRoleUseCase {
    /**
     * Ejecuta la desactivación de un rol.
     *
     * @param id identificador único del rol a desactivar
     */
    void execute(UUID id);
}
