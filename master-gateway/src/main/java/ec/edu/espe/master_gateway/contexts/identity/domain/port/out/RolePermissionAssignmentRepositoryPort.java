package ec.edu.espe.master_gateway.contexts.identity.domain.port.out;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida del dominio para la persistencia de asignaciones entre
 * roles y permisos.
 *
 * <p>Define las operaciones requeridas por el dominio para consultar y almacenar
 * las asignaciones de permisos a roles. Su implementación pertenece a la capa de
 * infraestructura, permitiendo que el dominio permanezca independiente de la
 * tecnología de persistencia utilizada y siguiendo los principios de la
 * Arquitectura Hexagonal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RolePermissionAssignmentRepositoryPort {

    List<RolePermissionAssignment> findByRoleId(UUID roleId);

    Optional<RolePermissionAssignment> findByRoleIdAndPermission(UUID roleId, Permission permission);

    Optional<RolePermissionAssignment> findByRoleIdAndPermissionIncludingInactive(UUID roleId, Permission permission);

    List<Permission> findPermissionsByRoleId(UUID roleId);

    RolePermissionAssignment save(RolePermissionAssignment assignment);
}
