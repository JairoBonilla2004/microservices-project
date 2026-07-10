package ec.edu.espe.master_gateway.contexts.module.domain.port.out;

import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import java.util.List;
import java.util.Optional;

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

    List<RoleModuleAssignment> findByRoleId(Long roleId);

    List<RoleModuleAssignment> findByModuleId(Long moduleId);

    Optional<RoleModuleAssignment> findByRoleIdAndModuleId(Long roleId, Long moduleId);

    List<Long> findModuleIdsByRoleId(Long roleId);

    RoleModuleAssignment save(RoleModuleAssignment assignment);

    void deleteByRoleIdAndModuleId(Long roleId, Long moduleId);
}
