package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.mapper.ModuleMapper;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ModuleRepositoryAdapterTest {

    @Mock
    private ModuleJpaRepository jpaRepository;

    @Mock
    private ModuleMapper mapper;

    private ModuleRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ModuleRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findById_shouldReturnModule_whenFound() {
        var id = UUID.randomUUID();
        var entity = new ModuleJpaEntity();
        var module = new Module("Test Module", "Description", "icon", 1);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(module);

        var result = adapter.findById(id);

        assertThat(result).isPresent().contains(module);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = adapter.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllModules() {
        var entity1 = new ModuleJpaEntity();
        var entity2 = new ModuleJpaEntity();
        var module1 = new Module("Module 1", "Desc 1", "icon1", 1);
        var module2 = new Module("Module 2", "Desc 2", "icon2", 2);

        when(jpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(module1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(module2);

        var result = adapter.findAll();

        assertThat(result).containsExactly(module1, module2);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoModules() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = adapter.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findAllActive_shouldReturnActiveModules() {
        var entity1 = new ModuleJpaEntity();
        var entity2 = new ModuleJpaEntity();
        var module1 = new Module("Module 1", "Desc 1", "icon1", 1);
        var module2 = new Module("Module 2", "Desc 2", "icon2", 2);

        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(module1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(module2);

        var result = adapter.findAllActive();

        assertThat(result).containsExactly(module1, module2);
    }

    @Test
    void findAllActive_shouldReturnEmptyList_whenNoActiveModules() {
        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistAndReturnModule() {
        var module = new Module("Test Module", "Description", "icon", 1);
        var entity = new ModuleJpaEntity();
        var savedEntity = new ModuleJpaEntity();

        when(mapper.toJpaEntity(module)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomainEntity(savedEntity)).thenReturn(module);

        var result = adapter.save(module);

        assertThat(result).isEqualTo(module);
    }

    @Test
    void save_shouldThrowDuplicateException_whenNombreDuplicated() {
        var module = new Module("Test Module", "Description", "icon", 1);
        var entity = new ModuleJpaEntity();

        when(mapper.toJpaEntity(module)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("duplicate key nombre"));

        var exception = assertThrows(DuplicateException.class, () -> adapter.save(module));

        assertThat(exception.getMessage()).contains("nombre");
    }

    @Test
    void save_shouldThrowPersistenceException_whenOtherConstraint() {
        var module = new Module("Test Module", "Description", "icon", 1);
        var entity = new ModuleJpaEntity();

        when(mapper.toJpaEntity(module)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("other error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.save(module));

        assertThat(exception.getMessage()).contains("Error al guardar el módulo");
    }

    @Test
    void existsByNombre_shouldReturnTrue_whenExists() {
        var nombre = "Test Module";

        when(jpaRepository.existsByNombre(nombre)).thenReturn(true);

        assertThat(adapter.existsByNombre(nombre)).isTrue();
    }

    @Test
    void existsByNombre_shouldReturnFalse_whenNotExists() {
        var nombre = "Test Module";

        when(jpaRepository.existsByNombre(nombre)).thenReturn(false);

        assertThat(adapter.existsByNombre(nombre)).isFalse();
    }
}
