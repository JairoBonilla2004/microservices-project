package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.mapper.IdentityMapper;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de repositorio para la entidad {@link Role}.
 *
 * <p>Implementa el puerto {@link RoleRepositoryPort} utilizando
 * Spring Data JPA y MapStruct para la conversión entre el modelo
 * de dominio y la entidad JPA. Convierte las excepciones de
 * integridad de datos en excepciones de dominio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository jpaRepository;
    private final IdentityMapper mapper;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository, IdentityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomainEntity);
    }

    @Override
    public List<Role> findAllActive() {
        return jpaRepository.findByEstado(EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public Role save(Role role) {
        try {
            RoleJpaEntity entity = mapper.toJpaEntity(role);
            RoleJpaEntity saved = jpaRepository.save(entity);
            return mapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            if (jpaRepository.existsByNombre(role.getNombre())) {
                throw new DuplicateException("Rol", "nombre", role.getNombre());
            }
            throw new PersistenceException("Error al guardar el rol", e);
        }
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRepository.existsByNombre(nombre);
    }
}
