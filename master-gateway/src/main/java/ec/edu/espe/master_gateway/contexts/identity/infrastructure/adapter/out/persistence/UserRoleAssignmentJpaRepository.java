package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Elimina físicamente (hard delete) la asignación usuario-rol de la tabla pivote.
     *
     * <p>A diferencia del resto de entidades, la revocación de un rol a un usuario
     * requiere borrado físico según el diseño del sistema: la tabla pivote no
     * conserva histórico de asignaciones revocadas, solo las vigentes.</p>
     */
    @Modifying
    @Query("DELETE FROM UserRoleAssignmentJpaEntity a WHERE a.userId = :userId AND a.roleId = :roleId")
    void hardDeleteByUserIdAndRoleId(@Param("userId") UUID userId, @Param("roleId") UUID roleId);
}
