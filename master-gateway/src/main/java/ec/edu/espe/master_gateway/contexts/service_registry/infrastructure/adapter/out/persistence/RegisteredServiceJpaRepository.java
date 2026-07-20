package ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link RegisteredServiceJpaEntity}.
 *
 * <p>Extiende {@link SoftDeleteRepository} para heredar el borrado
 * lógico y proporciona métodos de consulta específicos para la
 * gestión de servicios registrados.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RegisteredServiceJpaRepository extends SoftDeleteRepository<RegisteredServiceJpaEntity, UUID> {

    Optional<RegisteredServiceJpaEntity> findByServiceCode(String serviceCode);

    Optional<RegisteredServiceJpaEntity> findByServiceCodeAndEstado(String serviceCode, EstadoRegistro estado);

    List<RegisteredServiceJpaEntity> findByEstado(EstadoRegistro estado);

    List<RegisteredServiceJpaEntity> findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro estado);

    boolean existsByServiceCode(String serviceCode);

    boolean existsByServiceCodeAndEstado(String serviceCode, EstadoRegistro estado);
}
