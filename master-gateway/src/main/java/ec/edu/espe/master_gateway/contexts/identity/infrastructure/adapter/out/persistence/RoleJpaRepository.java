package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link RoleJpaEntity}.
 *
 * <p>Extiende {@link SoftDeleteRepository} para heredar el borrado
 * lógico y proporciona métodos de consulta adicionales específicos
 * para la entidad de rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RoleJpaRepository extends SoftDeleteRepository<RoleJpaEntity, UUID> {

    List<RoleJpaEntity> findByEstado(EstadoRegistro estado);

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndEstado(String nombre, EstadoRegistro estado);
}
