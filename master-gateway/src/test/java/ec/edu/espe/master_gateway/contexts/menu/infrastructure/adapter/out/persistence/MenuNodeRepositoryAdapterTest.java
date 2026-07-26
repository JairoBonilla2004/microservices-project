package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.mapper.MenuMapper;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuNodeRepositoryAdapterTest {

    @Mock
    private MenuNodeJpaRepository jpaRepository;

    @Mock
    private MenuMapper mapper;

    private MenuNodeRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MenuNodeRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findById_shouldReturnMenuNode_whenFound() {
        var id = UUID.randomUUID();
        var entity = new MenuNodeJpaEntity();
        var menuNode = new MenuNode("Test Menu", UUID.randomUUID(), null, 1);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(menuNode);

        var result = adapter.findById(id);

        assertThat(result).isPresent().contains(menuNode);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = adapter.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllActive_shouldReturnAllActiveOrdered() {
        var entity1 = new MenuNodeJpaEntity();
        var entity2 = new MenuNodeJpaEntity();
        var moduleId = UUID.randomUUID();
        var node1 = new MenuNode("Node 1", moduleId, null, 1);
        var node2 = new MenuNode("Node 2", moduleId, null, 2);

        when(jpaRepository.findByEstadoOrderByOrden(EstadoRegistro.ACTIVO)).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(node1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(node2);

        var result = adapter.findAllActive();

        assertThat(result).containsExactly(node1, node2);
    }

    @Test
    void findAllActive_shouldReturnEmptyList_whenNoActiveMenus() {
        when(jpaRepository.findByEstadoOrderByOrden(EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }

    @Test
    void findRootNodesByModuleIds_shouldReturnRootNodes_only() {
        var moduleId = UUID.randomUUID();
        var rootEntity = new MenuNodeJpaEntity();
        rootEntity.setParentId(null);
        var childEntity = new MenuNodeJpaEntity();
        childEntity.setParentId(UUID.randomUUID());
        var rootNode = new MenuNode("Root", moduleId, null, 1);
        var childNode = new MenuNode("Child", moduleId, UUID.randomUUID(), 2);

        when(jpaRepository.findTreeByModuleIds(List.of(moduleId))).thenReturn(List.of(rootEntity, childEntity));
        when(mapper.toDomainEntity(rootEntity)).thenReturn(rootNode);
        lenient().when(mapper.toDomainEntity(childEntity)).thenReturn(childNode);

        var result = adapter.findRootNodesByModuleIds(List.of(moduleId));

        assertThat(result).containsExactly(rootNode);
    }

    @Test
    void findRootNodesByModuleIds_shouldReturnEmpty_whenNoRootNodes() {
        var moduleId = UUID.randomUUID();
        var childEntity = new MenuNodeJpaEntity();
        childEntity.setParentId(UUID.randomUUID());

        when(jpaRepository.findTreeByModuleIds(List.of(moduleId))).thenReturn(List.of(childEntity));

        var result = adapter.findRootNodesByModuleIds(List.of(moduleId));

        assertThat(result).isEmpty();
    }

    @Test
    void findTreeByModuleIds_shouldReturnAllNodes() {
        var moduleId = UUID.randomUUID();
        var entity1 = new MenuNodeJpaEntity();
        var entity2 = new MenuNodeJpaEntity();
        var node1 = new MenuNode("Node 1", moduleId, null, 1);
        var node2 = new MenuNode("Node 2", moduleId, UUID.randomUUID(), 2);

        when(jpaRepository.findTreeByModuleIds(List.of(moduleId))).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(node1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(node2);

        var result = adapter.findTreeByModuleIds(List.of(moduleId));

        assertThat(result).containsExactly(node1, node2);
    }

    @Test
    void findTreeByModuleIds_shouldReturnEmpty_whenNoResults() {
        var moduleId = UUID.randomUUID();

        when(jpaRepository.findTreeByModuleIds(List.of(moduleId))).thenReturn(List.of());

        var result = adapter.findTreeByModuleIds(List.of(moduleId));

        assertThat(result).isEmpty();
    }

    @Test
    void findSubtreesByNodeIds_shouldReturnAllNodes() {
        var nodeId = UUID.randomUUID();
        var entity = new MenuNodeJpaEntity();
        var menuNode = new MenuNode("Node", UUID.randomUUID(), null, 1);

        when(jpaRepository.findSubtreesByNodeIds(List.of(nodeId))).thenReturn(List.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(menuNode);

        var result = adapter.findSubtreesByNodeIds(List.of(nodeId));

        assertThat(result).containsExactly(menuNode);
    }

    @Test
    void findSubtreesByNodeIds_shouldReturnEmpty_whenNoResults() {
        var nodeId = UUID.randomUUID();

        when(jpaRepository.findSubtreesByNodeIds(List.of(nodeId))).thenReturn(List.of());

        var result = adapter.findSubtreesByNodeIds(List.of(nodeId));

        assertThat(result).isEmpty();
    }

    @Test
    void findChildrenByParentId_shouldReturnChildren() {
        var parentId = UUID.randomUUID();
        var entity = new MenuNodeJpaEntity();
        var childNode = new MenuNode("Child", UUID.randomUUID(), parentId, 1);

        when(jpaRepository.findByParentIdAndEstado(parentId, EstadoRegistro.ACTIVO)).thenReturn(List.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(childNode);

        var result = adapter.findChildrenByParentId(parentId);

        assertThat(result).containsExactly(childNode);
    }

    @Test
    void findChildrenByParentId_shouldReturnEmpty_whenNoChildren() {
        var parentId = UUID.randomUUID();

        when(jpaRepository.findByParentIdAndEstado(parentId, EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findChildrenByParentId(parentId);

        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistAndReturnMenuNode() {
        var moduleId = UUID.randomUUID();
        var menuNode = new MenuNode("Test Menu", moduleId, null, 1);
        var entity = new MenuNodeJpaEntity();
        var savedEntity = new MenuNodeJpaEntity();

        when(mapper.toJpaEntity(menuNode)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomainEntity(savedEntity)).thenReturn(menuNode);

        var result = adapter.save(menuNode);

        assertThat(result).isEqualTo(menuNode);
    }
}
