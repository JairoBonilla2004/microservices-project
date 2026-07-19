package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.mapper.ModuleMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adaptador de repositorio para la entidad {@link RoleModuleAssignment}.
 *
 * <p>Implementa {@link RoleModuleAssignmentRepositoryPort} utilizando
 * {@link RoleModuleAssignmentJpaRepository} y {@link ModuleMapper}
 * para traducir entre el dominio y la persistencia JPA.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class RoleModuleAssignmentRepositoryAdapter implements RoleModuleAssignmentRepositoryPort {

    private final RoleModuleAssignmentJpaRepository jpaRepository;
    private final ModuleMapper mapper;

    @Autowired
    public RoleModuleAssignmentRepositoryAdapter(
            RoleModuleAssignmentJpaRepository jpaRepository, ModuleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RoleModuleAssignment> findByRoleId(UUID roleId) {
        return jpaRepository.findByRoleIdAndEstado(roleId, EstadoRegistro.ACTIVO)
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<RoleModuleAssignment> findByModuleId(UUID moduleId) {
        return jpaRepository.findByModuleIdAndEstado(moduleId, EstadoRegistro.ACTIVO)
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public Optional<RoleModuleAssignment> findByRoleIdAndModuleId(UUID roleId, UUID moduleId) {
        return jpaRepository.findByRoleIdAndModuleIdAndEstado(roleId, moduleId, EstadoRegistro.ACTIVO)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<UUID> findModuleIdsByRoleId(UUID roleId) {
        return jpaRepository.findModuleIdsByRoleId(roleId);
    }

    @Override
    public RoleModuleAssignment save(RoleModuleAssignment assignment) {
        RoleModuleAssignmentJpaEntity entity = mapper.toJpaEntity(assignment);
        RoleModuleAssignmentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomainEntity(saved);
    }


}
