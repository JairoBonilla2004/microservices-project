package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.mapper.ModuleMapper;
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
class RoleModuleAssignmentRepositoryAdapterTest {

    @Mock
    private RoleModuleAssignmentJpaRepository jpaRepository;

    @Mock
    private ModuleMapper mapper;

    private RoleModuleAssignmentRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RoleModuleAssignmentRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findByRoleId_shouldReturnAssignments() {
        var roleId = UUID.randomUUID();
        var entity1 = new RoleModuleAssignmentJpaEntity();
        var entity2 = new RoleModuleAssignmentJpaEntity();
        var assignment1 = new RoleModuleAssignment(roleId, UUID.randomUUID(), "admin");
        var assignment2 = new RoleModuleAssignment(roleId, UUID.randomUUID(), "admin");

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
    void findByModuleId_shouldReturnAssignments() {
        var moduleId = UUID.randomUUID();
        var entity = new RoleModuleAssignmentJpaEntity();
        var assignment = new RoleModuleAssignment(UUID.randomUUID(), moduleId, "admin");

        when(jpaRepository.findByModuleIdAndEstado(moduleId, EstadoRegistro.ACTIVO)).thenReturn(List.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(assignment);

        var result = adapter.findByModuleId(moduleId);

        assertThat(result).containsExactly(assignment);
    }

    @Test
    void findByModuleId_shouldReturnEmpty_whenNoAssignments() {
        var moduleId = UUID.randomUUID();

        when(jpaRepository.findByModuleIdAndEstado(moduleId, EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findByModuleId(moduleId);

        assertThat(result).isEmpty();
    }

    @Test
    void findByRoleIdAndModuleId_shouldReturnAssignment_whenFound() {
        var roleId = UUID.randomUUID();
        var moduleId = UUID.randomUUID();
        var entity = new RoleModuleAssignmentJpaEntity();
        var assignment = new RoleModuleAssignment(roleId, moduleId, "admin");

        when(jpaRepository.findByRoleIdAndModuleIdAndEstado(roleId, moduleId, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(assignment);

        var result = adapter.findByRoleIdAndModuleId(roleId, moduleId);

        assertThat(result).isPresent().contains(assignment);
    }

    @Test
    void findByRoleIdAndModuleId_shouldReturnEmpty_whenNotFound() {
        var roleId = UUID.randomUUID();
        var moduleId = UUID.randomUUID();

        when(jpaRepository.findByRoleIdAndModuleIdAndEstado(roleId, moduleId, EstadoRegistro.ACTIVO))
                .thenReturn(Optional.empty());

        var result = adapter.findByRoleIdAndModuleId(roleId, moduleId);

        assertThat(result).isEmpty();
    }

    @Test
    void findModuleIdsByRoleId_shouldReturnModuleIds() {
        var roleId = UUID.randomUUID();
        var moduleId1 = UUID.randomUUID();
        var moduleId2 = UUID.randomUUID();

        when(jpaRepository.findModuleIdsByRoleId(roleId)).thenReturn(List.of(moduleId1, moduleId2));

        var result = adapter.findModuleIdsByRoleId(roleId);

        assertThat(result).containsExactly(moduleId1, moduleId2);
    }

    @Test
    void findModuleIdsByRoleId_shouldReturnEmpty_whenNoAssignments() {
        var roleId = UUID.randomUUID();

        when(jpaRepository.findModuleIdsByRoleId(roleId)).thenReturn(List.of());

        var result = adapter.findModuleIdsByRoleId(roleId);

        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistAndReturnAssignment() {
        var roleId = UUID.randomUUID();
        var moduleId = UUID.randomUUID();
        var assignment = new RoleModuleAssignment(roleId, moduleId, "admin");
        var entity = new RoleModuleAssignmentJpaEntity();
        var savedEntity = new RoleModuleAssignmentJpaEntity();

        when(mapper.toJpaEntity(assignment)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomainEntity(savedEntity)).thenReturn(assignment);

        var result = adapter.save(assignment);

        assertThat(result).isEqualTo(assignment);
    }
}
