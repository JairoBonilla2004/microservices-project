package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.UUID;

/**
 * Caso de uso para la eliminación de un permiso de un rol.
 *
 * <p>Remueve un permiso previamente asignado a un rol. Valida que la
 * asignación exista antes de proceder con la eliminación.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RemovePermissionFromRoleUseCase {
    void execute(UUID roleId, Permission permission);
}
