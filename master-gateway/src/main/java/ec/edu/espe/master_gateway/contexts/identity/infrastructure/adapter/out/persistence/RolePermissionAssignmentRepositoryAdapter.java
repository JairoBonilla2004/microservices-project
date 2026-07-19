package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

/**
 * Adaptador de repositorio para la entidad {@link RolePermissionAssignment}.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort}
 * utilizando Spring Data JPA y MapStruct para la conversión entre el modelo de dominio
 * y la entidad JPA.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.mapper.IdentityMapper;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RolePermissionAssignmentRepositoryAdapter implements RolePermissionAssignmentRepositoryPort {

    private final RolePermissionAssignmentJpaRepository jpaRepository;
    private final IdentityMapper mapper;

    public RolePermissionAssignmentRepositoryAdapter(RolePermissionAssignmentJpaRepository jpaRepository,
                                                     IdentityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RolePermissionAssignment> findByRoleId(UUID roleId) {
        return jpaRepository.findByRoleIdAndEstado(roleId, EstadoRegistro.ACTIVO)
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public Optional<RolePermissionAssignment> findByRoleIdAndPermission(UUID roleId, Permission permission) {
        return jpaRepository.findByRoleIdAndPermissionAndEstado(roleId, permission, EstadoRegistro.ACTIVO)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Permission> findPermissionsByRoleId(UUID roleId) {
        return jpaRepository.findPermissionsByRoleId(roleId);
    }

    @Override
    public RolePermissionAssignment save(RolePermissionAssignment assignment) {
        RolePermissionAssignmentJpaEntity entity = mapper.toJpaEntity(assignment);
        RolePermissionAssignmentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomainEntity(saved);
    }
}
