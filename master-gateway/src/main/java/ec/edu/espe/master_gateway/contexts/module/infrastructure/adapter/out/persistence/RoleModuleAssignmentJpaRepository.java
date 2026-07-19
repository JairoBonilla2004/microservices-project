package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad {@link RoleModuleAssignmentJpaEntity}.
 *
 * <p>Proporciona operaciones de acceso a datos para la tabla
 * {@code roles_modules}, incluyendo consultas por rol, por módulo
 * y eliminación de asignaciones específicas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RoleModuleAssignmentJpaRepository
        extends SoftDeleteRepository<RoleModuleAssignmentJpaEntity, UUID> {

    /**
     * Busca todas las asignaciones de un rol.
     *
     * @param roleId identificador del rol.
     * @return lista de asignaciones del rol.
     */
    List<RoleModuleAssignmentJpaEntity> findByRoleIdAndEstado(UUID roleId, EstadoRegistro estado);

    /**
     * Busca todas las asignaciones de un módulo.
     *
     * @param moduleId identificador del módulo.
     * @return lista de asignaciones del módulo.
     */
    List<RoleModuleAssignmentJpaEntity> findByModuleIdAndEstado(UUID moduleId, EstadoRegistro estado);

    /**
     * Busca una asignación específica por rol y módulo.
     *
     * @param roleId   identificador del rol.
     * @param moduleId identificador del módulo.
     * @return la asignación si existe.
     */
    Optional<RoleModuleAssignmentJpaEntity> findByRoleIdAndModuleIdAndEstado(UUID roleId, UUID moduleId, EstadoRegistro estado);

    /**
     * Obtiene los identificadores de los módulos asignados a un rol.
     *
     * @param roleId identificador del rol.
     * @return lista de IDs de módulos asignados al rol.
     */
    @Query("SELECT a.moduleId FROM RoleModuleAssignmentJpaEntity a WHERE a.roleId = :roleId AND a.estado = 'ACTIVO'")
    List<UUID> findModuleIdsByRoleId(@Param("roleId") UUID roleId);

}
