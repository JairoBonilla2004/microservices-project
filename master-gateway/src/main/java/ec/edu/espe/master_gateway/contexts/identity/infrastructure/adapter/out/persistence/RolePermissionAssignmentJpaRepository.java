package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

/**
 * Repositorio JPA para la entidad {@link RolePermissionAssignmentJpaEntity}.
 *
 * <p>Extiende {@link ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository}
 * para heredar el borrado lógico y proporciona métodos de consulta específicos para
 * la gestión de asignaciones entre roles y permisos.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolePermissionAssignmentJpaRepository
        extends SoftDeleteRepository<RolePermissionAssignmentJpaEntity, UUID> {

    List<RolePermissionAssignmentJpaEntity> findByRoleIdAndEstado(UUID roleId, EstadoRegistro estado);

    Optional<RolePermissionAssignmentJpaEntity> findByRoleIdAndPermissionAndEstado(
            UUID roleId, Permission permission, EstadoRegistro estado);

    Optional<RolePermissionAssignmentJpaEntity> findByRoleIdAndPermission(UUID roleId, Permission permission);

    @Query("SELECT a.permission FROM RolePermissionAssignmentJpaEntity a WHERE a.roleId = :roleId AND a.estado = 'ACTIVO'")
    List<Permission> findPermissionsByRoleId(@Param("roleId") UUID roleId);
}
