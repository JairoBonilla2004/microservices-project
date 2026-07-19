package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.mapper.ModuleMapper;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Adaptador de repositorio para la entidad {@link Module}.
 *
 * <p>Implementa {@link ModuleRepositoryPort} utilizando
 * {@link ModuleJpaRepository} y {@link ModuleMapper} para traducir
 * entre el dominio y la persistencia JPA.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class ModuleRepositoryAdapter implements ModuleRepositoryPort {

    private final ModuleJpaRepository jpaRepository;
    private final ModuleMapper mapper;

    @Autowired
    public ModuleRepositoryAdapter(ModuleJpaRepository jpaRepository, ModuleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Module> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Module> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<Module> findAllActive() {
        return jpaRepository.findByEstado(EstadoRegistro.ACTIVO)
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public Module save(Module module) {
        try {
            ModuleJpaEntity entity = mapper.toJpaEntity(module);
            ModuleJpaEntity saved = jpaRepository.save(entity);
            return mapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("nombre")) {
                throw new DuplicateException("Module", "nombre", module.getNombre());
            }
            throw new PersistenceException("Error al guardar el módulo", e);
        }
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRepository.existsByNombre(nombre);
    }
}
