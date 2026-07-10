package ec.edu.espe.master_gateway.contexts.menu.domain.port.out;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad {@link RoleMenuAssignment}.
 *
 * <p>Define las operaciones de persistencia para gestionar asignaciones
 * entre roles y nodos de menú, incluyendo consultas por rol y
 * eliminación de asignaciones específicas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RoleMenuAssignmentRepositoryPort {

    List<RoleMenuAssignment> findByRoleId(Long roleId);

    List<Long> findMenuNodeIdsByRoleId(Long roleId);

    Optional<RoleMenuAssignment> findByRoleIdAndMenuNodeId(Long roleId, Long menuNodeId);

    RoleMenuAssignment save(RoleMenuAssignment assignment);

    void deleteByRoleIdAndMenuNodeId(Long roleId, Long menuNodeId);
}
