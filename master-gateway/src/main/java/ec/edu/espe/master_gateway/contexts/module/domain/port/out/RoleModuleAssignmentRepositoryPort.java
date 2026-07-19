package ec.edu.espe.master_gateway.contexts.module.domain.port.out;

import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de repositorio para la entidad {@link RoleModuleAssignment}.
 *
 * <p>Define las operaciones de persistencia necesarias para gestionar
 * las asignaciones entre roles y módulos, incluyendo consultas por
 * rol, por módulo, y eliminación de asignaciones específicas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RoleModuleAssignmentRepositoryPort {

    List<RoleModuleAssignment> findByRoleId(UUID roleId);

    List<RoleModuleAssignment> findByModuleId(UUID moduleId);

    Optional<RoleModuleAssignment> findByRoleIdAndModuleId(UUID roleId, UUID moduleId);

    List<UUID> findModuleIdsByRoleId(UUID roleId);

    RoleModuleAssignment save(RoleModuleAssignment assignment);
}
