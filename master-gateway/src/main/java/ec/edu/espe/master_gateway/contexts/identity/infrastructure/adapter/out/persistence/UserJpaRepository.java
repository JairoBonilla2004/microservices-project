package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repositorio JPA para la entidad {@link UserJpaEntity}.
 *
 * <p>Extiende {@link SoftDeleteRepository} para heredar el borrado
 * lógico y proporciona métodos de consulta adicionales específicos
 * para la entidad de usuario.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UserJpaRepository extends SoftDeleteRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByUsername(String username);

    List<UserJpaEntity> findByEstado(EstadoRegistro estado);

    Page<UserJpaEntity> findByEstado(EstadoRegistro estado, Pageable pageable);

    List<UserJpaEntity> findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro estado);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
