package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para la consulta de los permisos asignados a un rol.
 *
 * <p>Recupera la lista de todos los permisos asociados a un rol específico.
 * Si el rol no existe, debe lanzar una excepción de tipo
 * {@code ResourceNotFoundException}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetRolePermissionsUseCase {
    List<Permission> execute(UUID roleId);
}
