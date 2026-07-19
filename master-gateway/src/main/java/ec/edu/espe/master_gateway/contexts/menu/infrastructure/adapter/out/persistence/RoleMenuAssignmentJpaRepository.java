package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad {@link RoleMenuAssignmentJpaEntity}.
 *
 * <p>Extiende {@link SoftDeleteRepository} para heredar el borrado
 * lógico y expone métodos de consulta para gestionar las asignaciones
 * entre roles y nodos de menú.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RoleMenuAssignmentJpaRepository extends SoftDeleteRepository<RoleMenuAssignmentJpaEntity, UUID> {

    List<RoleMenuAssignmentJpaEntity> findByRoleId(UUID roleId);

    List<RoleMenuAssignmentJpaEntity> findByRoleIdAndEstado(UUID roleId, EstadoRegistro estado);

    Optional<RoleMenuAssignmentJpaEntity> findByRoleIdAndMenuNodeId(UUID roleId, UUID menuNodeId);

    Optional<RoleMenuAssignmentJpaEntity> findByRoleIdAndMenuNodeIdAndEstado(UUID roleId, UUID menuNodeId, EstadoRegistro estado);

    @Query("SELECT a.menuNodeId FROM RoleMenuAssignmentJpaEntity a WHERE a.roleId = :roleId AND a.estado = 'ACTIVO'")
    List<UUID> findMenuNodeIdsByRoleId(@Param("roleId") UUID roleId);

}
