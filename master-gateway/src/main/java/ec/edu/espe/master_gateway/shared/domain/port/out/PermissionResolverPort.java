package ec.edu.espe.master_gateway.shared.domain.port.out;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.Set;

/**
 * Puerto de salida para la resolución de permisos por rol.
 *
 * <p>Define el contrato para obtener el conjunto de permisos asociados
 * a un nombre de rol específico dentro del sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface PermissionResolverPort {

    Set<Permission> resolvePermissions(String roleName);
}
