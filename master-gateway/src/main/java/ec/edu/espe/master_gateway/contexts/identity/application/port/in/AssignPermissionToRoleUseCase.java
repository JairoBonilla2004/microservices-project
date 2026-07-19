package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignPermissionToRoleRequest;
import java.util.UUID;

/**
 * Caso de uso para la asignación de un permiso a un rol.
 *
 * <p>Asocia un permiso existente a un rol del sistema. Valida que tanto
 * el rol como el permiso existan y que el permiso no esté ya asignado
 * al rol especificado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface AssignPermissionToRoleUseCase {
    void execute(UUID roleId, AssignPermissionToRoleRequest request);
}
