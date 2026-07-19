package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para la revocación de un rol asignado a un usuario.
 *
 * <p>Elimina la asociación entre un usuario y un rol, retirando los
 * permisos asociados a dicho rol para el usuario especificado.
 * Valida que la asignación exista antes de proceder.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RevokeRoleUseCase {
    /**
     * Ejecuta la revocación de un rol a un usuario.
     *
     * @param userId identificador único del usuario
     * @param roleId identificador único del rol a revocar
     */
    void execute(UUID userId, UUID roleId);
}
