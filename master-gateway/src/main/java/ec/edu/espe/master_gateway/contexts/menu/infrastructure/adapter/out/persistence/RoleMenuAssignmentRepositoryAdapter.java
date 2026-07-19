package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.mapper.MenuMapper;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Adaptador de repositorio para la entidad {@link RoleMenuAssignment}.
 *
 * <p>Implementa {@link RoleMenuAssignmentRepositoryPort} utilizando
 * {@link RoleMenuAssignmentJpaRepository} para la persistencia y
 * {@link MenuMapper} para la conversi&oacute;n entre el dominio
 * y la entidad JPA.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class RoleMenuAssignmentRepositoryAdapter implements RoleMenuAssignmentRepositoryPort {

    private final RoleMenuAssignmentJpaRepository jpaRepository;
    private final MenuMapper mapper;

    public RoleMenuAssignmentRepositoryAdapter(RoleMenuAssignmentJpaRepository jpaRepository, MenuMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RoleMenuAssignment> findByRoleId(UUID roleId) {
        return jpaRepository.findByRoleIdAndEstado(roleId, EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> findMenuNodeIdsByRoleId(UUID roleId) {
        return jpaRepository.findMenuNodeIdsByRoleId(roleId);
    }

    @Override
    public Optional<RoleMenuAssignment> findByRoleIdAndMenuNodeId(UUID roleId, UUID menuNodeId) {
        return jpaRepository.findByRoleIdAndMenuNodeIdAndEstado(roleId, menuNodeId, EstadoRegistro.ACTIVO)
                .map(mapper::toDomainEntity);
    }

    @Override
    public RoleMenuAssignment save(RoleMenuAssignment assignment) {
        var entity = mapper.toJpaEntity(assignment);
        var saved = jpaRepository.save(entity);
        return mapper.toDomainEntity(saved);
    }


}
