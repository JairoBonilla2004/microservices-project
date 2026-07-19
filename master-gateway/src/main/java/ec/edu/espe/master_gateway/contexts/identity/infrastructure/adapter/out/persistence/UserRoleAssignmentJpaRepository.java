package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link UserRoleAssignmentJpaEntity}.
 *
 * <p>Extiende {@link SoftDeleteRepository} para heredar el borrado
 * lógico y proporciona métodos de consulta específicos para las
 * asignaciones entre usuarios y roles.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UserRoleAssignmentJpaRepository
        extends SoftDeleteRepository<UserRoleAssignmentJpaEntity, UUID> {

    List<UserRoleAssignmentJpaEntity> findByUserId(UUID userId);

    List<UserRoleAssignmentJpaEntity> findByRoleId(UUID roleId);

    Optional<UserRoleAssignmentJpaEntity> findByUserIdAndRoleId(UUID userId, UUID roleId);

    List<UserRoleAssignmentJpaEntity> findByUserIdAndEstado(UUID userId, EstadoRegistro estado);

    List<UserRoleAssignmentJpaEntity> findByRoleIdAndEstado(UUID roleId, EstadoRegistro estado);

    Optional<UserRoleAssignmentJpaEntity> findByUserIdAndRoleIdAndEstado(UUID userId, UUID roleId, EstadoRegistro estado);
}
