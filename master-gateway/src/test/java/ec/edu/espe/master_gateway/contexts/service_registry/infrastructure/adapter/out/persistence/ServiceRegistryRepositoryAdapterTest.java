package ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService.ValidationMode;
import ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.mapper.ServiceRegistryMapper;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ServiceRegistryRepositoryAdapterTest {

    @Mock
    private RegisteredServiceJpaRepository jpaRepository;

    @Mock
    private ServiceRegistryMapper mapper;

    private ServiceRegistryRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ServiceRegistryRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findByServiceCode_shouldReturnService_whenFound() {
        var serviceCode = "SVC-001";
        var entity = new RegisteredServiceJpaEntity();
        var service = new RegisteredService(serviceCode, "Test Service", "http://localhost", ValidationMode.DELEGATE);

        when(jpaRepository.findByServiceCodeAndEstado(serviceCode, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(service);

        var result = adapter.findByServiceCode(serviceCode);

        assertThat(result).isPresent().contains(service);
    }

    @Test
    void findByServiceCode_shouldReturnEmpty_whenNotFound() {
        var serviceCode = "SVC-001";

        when(jpaRepository.findByServiceCodeAndEstado(serviceCode, EstadoRegistro.ACTIVO)).thenReturn(Optional.empty());

        var result = adapter.findByServiceCode(serviceCode);

        assertThat(result).isEmpty();
    }

    @Test
    void findByServiceCode_shouldThrowPersistenceException_onDataIntegrity() {
        var serviceCode = "SVC-001";

        when(jpaRepository.findByServiceCodeAndEstado(serviceCode, EstadoRegistro.ACTIVO))
                .thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.findByServiceCode(serviceCode));

        assertThat(exception.getMessage()).contains("Error al buscar el servicio por código");
    }

    @Test
    void findAllActive_shouldReturnAllActiveServices() {
        var entity1 = new RegisteredServiceJpaEntity();
        var entity2 = new RegisteredServiceJpaEntity();
        var service1 = new RegisteredService("SVC-001", "Service 1", "http://localhost", ValidationMode.DELEGATE);
        var service2 = new RegisteredService("SVC-002", "Service 2", "http://localhost", ValidationMode.LOCAL);

        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(service1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(service2);

        var result = adapter.findAllActive();

        assertThat(result).containsExactly(service1, service2);
    }

    @Test
    void findAllActive_shouldReturnEmptyList_whenNoActiveServices() {
        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }

    @Test
    void findAllActive_shouldThrowPersistenceException_onDataIntegrity() {
        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO))
                .thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.findAllActive());

        assertThat(exception.getMessage()).contains("Error al listar los servicios activos");
    }

    @Test
    void save_shouldPersistAndReturnService() {
        var service = new RegisteredService("SVC-001", "Test Service", "http://localhost", ValidationMode.DELEGATE);
        var entity = new RegisteredServiceJpaEntity();
        var savedEntity = new RegisteredServiceJpaEntity();

        when(mapper.toJpaEntity(service)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomainEntity(savedEntity)).thenReturn(service);

        var result = adapter.save(service);

        assertThat(result).isEqualTo(service);
    }

    @Test
    void save_shouldThrowPersistenceException_onDataIntegrity() {
        var service = new RegisteredService("SVC-001", "Test Service", "http://localhost", ValidationMode.DELEGATE);
        var entity = new RegisteredServiceJpaEntity();

        when(mapper.toJpaEntity(service)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.save(service));

        assertThat(exception.getMessage()).contains("Error al guardar el servicio registrado");
    }

    @Test
    void existsByServiceCode_shouldReturnTrue_whenExists() {
        var serviceCode = "SVC-001";

        when(jpaRepository.existsByServiceCode(serviceCode)).thenReturn(true);

        assertThat(adapter.existsByServiceCode(serviceCode)).isTrue();
    }

    @Test
    void existsByServiceCode_shouldReturnFalse_whenNotExists() {
        var serviceCode = "SVC-001";

        when(jpaRepository.existsByServiceCode(serviceCode)).thenReturn(false);

        assertThat(adapter.existsByServiceCode(serviceCode)).isFalse();
    }

    @Test
    void existsByServiceCode_shouldThrowPersistenceException_onDataIntegrity() {
        var serviceCode = "SVC-001";

        when(jpaRepository.existsByServiceCode(serviceCode))
                .thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.existsByServiceCode(serviceCode));

        assertThat(exception.getMessage()).contains("Error al verificar la existencia del servicio por código");
    }
}
