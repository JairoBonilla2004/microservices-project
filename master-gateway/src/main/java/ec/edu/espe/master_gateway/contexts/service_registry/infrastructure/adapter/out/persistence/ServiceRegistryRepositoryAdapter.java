package ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.mapper.ServiceRegistryMapper;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que implementa {@link ServiceRegistryRepositoryPort}
 * utilizando JPA como tecnología de persistencia.
 *
 * <p>Traduce las operaciones del dominio en llamadas al repositorio
 * JPA y al mapper correspondiente, manejando las excepciones de
 * integridad de datos y transformándolas en excepciones de dominio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class ServiceRegistryRepositoryAdapter implements ServiceRegistryRepositoryPort {

    private final RegisteredServiceJpaRepository jpaRepository;
    private final ServiceRegistryMapper mapper;

    public ServiceRegistryRepositoryAdapter(RegisteredServiceJpaRepository jpaRepository, ServiceRegistryMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<RegisteredService> findByServiceCode(String serviceCode) {
        try {
            return jpaRepository.findByServiceCodeAndEstado(serviceCode, EstadoRegistro.ACTIVO)
                    .map(mapper::toDomainEntity);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al buscar el servicio por código", e);
        }
    }

    @Override
    public List<RegisteredService> findAllActive() {
        try {
            return jpaRepository.findByEstado(EstadoRegistro.ACTIVO).stream()
                    .map(mapper::toDomainEntity)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al listar los servicios activos", e);
        }
    }

    @Override
    public RegisteredService save(RegisteredService service) {
        try {
            RegisteredServiceJpaEntity entity = mapper.toJpaEntity(service);
            RegisteredServiceJpaEntity saved = jpaRepository.save(entity);
            return mapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al guardar el servicio registrado", e);
        }
    }

    @Override
    public boolean existsByServiceCode(String serviceCode) {
        try {
            return jpaRepository.existsByServiceCode(serviceCode);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al verificar la existencia del servicio por código", e);
        }
    }

}
