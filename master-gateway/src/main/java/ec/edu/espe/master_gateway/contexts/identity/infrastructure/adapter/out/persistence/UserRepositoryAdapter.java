package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.mapper.IdentityMapper;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.PageResult;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de repositorio para la entidad {@link User}.
 *
 * <p>Implementa el puerto {@link UserRepositoryPort} utilizando
 * Spring Data JPA y MapStruct para la conversión entre el modelo
 * de dominio y la entidad JPA. Convierte las excepciones de
 * integridad de datos en excepciones de dominio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final IdentityMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository, IdentityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(mapper::toDomainEntity);
    }

    @Override
    public List<User> findAllActive() {
        return jpaRepository.findByEstado(EstadoRegistro.ACTIVO).stream()
                .map(mapper::toDomainEntity)
                .toList();
    }

    @Override
    public PageResult<User> findActivePage(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        var result = jpaRepository.findByEstado(EstadoRegistro.ACTIVO, pageable);
        var content = result.getContent().stream().map(mapper::toDomainEntity).toList();
        return new PageResult<>(content, result.getTotalElements(), page, size);
    }

    @Override
    public User save(User user) {
        try {
            UserJpaEntity entity = mapper.toJpaEntity(user);
            UserJpaEntity saved = jpaRepository.save(entity);
            return mapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            if (jpaRepository.existsByUsername(user.getUsername())) {
                throw new DuplicateException("Usuario", "username", user.getUsername());
            }
            if (jpaRepository.existsByEmail(user.getEmail())) {
                throw new DuplicateException("Usuario", "email", user.getEmail());
            }
            throw new PersistenceException("Error al guardar el usuario", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
