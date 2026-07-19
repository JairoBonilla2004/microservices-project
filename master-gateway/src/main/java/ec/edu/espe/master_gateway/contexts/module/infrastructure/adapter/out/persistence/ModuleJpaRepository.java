package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link ModuleJpaEntity}.
 *
 * <p>Proporciona operaciones de acceso a datos para la tabla
 * {@code modules}, incluyendo verificación de unicidad por nombre.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ModuleJpaRepository extends SoftDeleteRepository<ModuleJpaEntity, UUID> {

    /**
     * Verifica si existe un módulo con el nombre dado.
     *
     * @param nombre nombre del módulo a buscar.
     * @return {@code true} si ya existe un módulo con ese nombre.
     */
    boolean existsByNombre(String nombre);

    List<ModuleJpaEntity> findByEstado(EstadoRegistro estado);

    boolean existsByNombreAndEstado(String nombre, EstadoRegistro estado);
}
