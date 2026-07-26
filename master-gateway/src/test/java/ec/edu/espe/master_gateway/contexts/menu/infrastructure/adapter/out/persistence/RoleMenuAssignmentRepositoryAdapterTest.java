package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
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
class RoleMenuAssignmentRepositoryAdapterTest {

    @Mock
    private RoleMenuAssignmentJpaRepository jpaRepository;

    @Mock
    private MenuMapper mapper;

    private RoleMenuAssignmentRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RoleMenuAssignmentRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findByRoleId_shouldReturnAssignments() {
        var roleId = UUID.randomUUID();
        var entity1 = new RoleMenuAssignmentJpaEntity();
        var entity2 = new RoleMenuAssignmentJpaEntity();
        var assignment1 = new RoleMenuAssignment(roleId, UUID.randomUUID(), "admin");
        var assignment2 = new RoleMenuAssignment(roleId, UUID.randomUUID(), "admin");

        when(jpaRepository.findByRoleIdAndEstado(roleId, EstadoRegistro.ACTIVO)).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(assignment1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(assignment2);

        var result = adapter.findByRoleId(roleId);

        assertThat(result).containsExactly(assignment1, assignment2);
    }

    @Test
    void findByRoleId_shouldReturnEmpty_whenNoAssignments() {
        var roleId = UUID.randomUUID();

        when(jpaRepository.findByRoleIdAndEstado(roleId, EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findByRoleId(roleId);

        assertThat(result).isEmpty();
    }

    @Test
    void findMenuNodeIdsByRoleId_shouldReturnNodeIds() {
        var roleId = UUID.randomUUID();
        var nodeId1 = UUID.randomUUID();
        var nodeId2 = UUID.randomUUID();

        when(jpaRepository.findMenuNodeIdsByRoleId(roleId)).thenReturn(List.of(nodeId1, nodeId2));

        var result = adapter.findMenuNodeIdsByRoleId(roleId);

        assertThat(result).containsExactly(nodeId1, nodeId2);
    }

    @Test
    void findMenuNodeIdsByRoleId_shouldReturnEmpty_whenNoAssignments() {
        var roleId = UUID.randomUUID();

        when(jpaRepository.findMenuNodeIdsByRoleId(roleId)).thenReturn(List.of());

        var result = adapter.findMenuNodeIdsByRoleId(roleId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByRoleIdAndMenuNodeId_shouldReturnAssignment_whenFound() {
        var roleId = UUID.randomUUID();
        var menuNodeId = UUID.randomUUID();
        var entity = new RoleMenuAssignmentJpaEntity();
        var assignment = new RoleMenuAssignment(roleId, menuNodeId, "admin");

        when(jpaRepository.findByRoleIdAndMenuNodeIdAndEstado(roleId, menuNodeId, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(assignment);

        var result = adapter.findByRoleIdAndMenuNodeId(roleId, menuNodeId);

        assertThat(result).isPresent().contains(assignment);
    }

    @Test
    void findByRoleIdAndMenuNodeId_shouldReturnEmpty_whenNotFound() {
        var roleId = UUID.randomUUID();
        var menuNodeId = UUID.randomUUID();

        when(jpaRepository.findByRoleIdAndMenuNodeIdAndEstado(roleId, menuNodeId, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.empty());

        var result = adapter.findByRoleIdAndMenuNodeId(roleId, menuNodeId);

        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistAndReturnAssignment() {
        var roleId = UUID.randomUUID();
        var menuNodeId = UUID.randomUUID();
        var assignment = new RoleMenuAssignment(roleId, menuNodeId, "admin");
        var entity = new RoleMenuAssignmentJpaEntity();
        var savedEntity = new RoleMenuAssignmentJpaEntity();

        when(mapper.toJpaEntity(assignment)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomainEntity(savedEntity)).thenReturn(assignment);

        var result = adapter.save(assignment);

        assertThat(result).isEqualTo(assignment);
    }
}
