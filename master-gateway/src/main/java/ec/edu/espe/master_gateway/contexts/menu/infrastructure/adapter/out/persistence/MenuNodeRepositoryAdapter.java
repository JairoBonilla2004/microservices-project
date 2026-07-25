package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.mapper.MenuMapper;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Adaptador de repositorio para la entidad {@link MenuNode}.
 *
 * <p>Implementa {@link MenuRepositoryPort} utilizando
 * {@link MenuNodeJpaRepository} para la persistencia y
 * {@link MenuMapper} para la conversi&oacute;n entre el
 * dominio y la entidad JPA.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class MenuNodeRepositoryAdapter implements MenuRepositoryPort {

    private final MenuNodeJpaRepository jpaRepository;
    private final MenuMapper mapper;

    public MenuNodeRepositoryAdapter(MenuNodeJpaRepository jpaRepository, MenuMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<MenuNode> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<MenuNode> findAllActive() {
        return jpaRepository.findByEstadoOrderByOrden(EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<MenuNode> findRootNodesByModuleIds(List<UUID> moduleIds) {
        return jpaRepository.findTreeByModuleIds(moduleIds).stream()
                .filter(e -> e.getParentId() == null)
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<MenuNode> findTreeByModuleIds(List<UUID> moduleIds) {
        return jpaRepository.findTreeByModuleIds(moduleIds).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<MenuNode> findSubtreesByNodeIds(List<UUID> nodeIds) {
        return jpaRepository.findSubtreesByNodeIds(nodeIds).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<MenuNode> findChildrenByParentId(UUID parentId) {
        return jpaRepository.findByParentIdAndEstado(parentId, EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public MenuNode save(MenuNode menuNode) {
        var entity = mapper.toJpaEntity(menuNode);
        var saved = jpaRepository.save(entity);
        return mapper.toDomainEntity(saved);
    }
}
