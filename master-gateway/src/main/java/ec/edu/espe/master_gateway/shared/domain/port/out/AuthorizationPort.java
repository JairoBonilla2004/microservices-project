package ec.edu.espe.master_gateway.shared.domain.port.out;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.UUID;

/**
 * Puerto de salida para operaciones de autorización.
 *
 * <p>Define los métodos necesarios para verificar permisos de usuario,
 * obtener el identificador del usuario autenticado, y validar la
 * pertenencia de recursos dentro del sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface AuthorizationPort {

    UUID getCurrentUserId();

    boolean hasPermission(Permission permission);

    void requirePermission(Permission permission);

    void requireOwnershipOrPermission(UUID resourceOwnerId, Permission requiredPermission);
}
