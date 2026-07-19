package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.mapper.IdentityMapper;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de repositorio para la entidad {@link UserRoleAssignment}.
 *
 * <p>Implementa el puerto {@link UserRoleAssignmentRepositoryPort}
 * utilizando Spring Data JPA y MapStruct para la conversión entre
 * el modelo de dominio y la entidad JPA. Convierte las excepciones
 * de integridad de datos en excepciones de dominio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class UserRoleAssignmentRepositoryAdapter implements UserRoleAssignmentRepositoryPort {

    private final UserRoleAssignmentJpaRepository jpaRepository;
    private final IdentityMapper mapper;

    public UserRoleAssignmentRepositoryAdapter(
            UserRoleAssignmentJpaRepository jpaRepository, IdentityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserRoleAssignment> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndEstado(userId, EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<UserRoleAssignment> findByRoleId(UUID roleId) {
        return jpaRepository.findByRoleIdAndEstado(roleId, EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public Optional<UserRoleAssignment> findByUserIdAndRoleId(UUID userId, UUID roleId) {
        return jpaRepository.findByUserIdAndRoleIdAndEstado(userId, roleId, EstadoRegistro.ACTIVO)
                .map(mapper::toDomainEntity);
    }

    @Override
    public UserRoleAssignment save(UserRoleAssignment assignment) {
        try {
            UserRoleAssignmentJpaEntity entity = mapper.toJpaEntity(assignment);
            UserRoleAssignmentJpaEntity saved = jpaRepository.save(entity);
            return mapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException(
                    "AsignacionUsuarioRol",
                    "userId-roleId",
                    assignment.getUserId() + "-" + assignment.getRoleId());
        }
    }

}
