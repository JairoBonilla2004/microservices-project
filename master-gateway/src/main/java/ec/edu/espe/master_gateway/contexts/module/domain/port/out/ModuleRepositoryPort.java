package ec.edu.espe.master_gateway.contexts.module.domain.port.out;

import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad {@link Module}.
 *
 * <p>Define las operaciones de persistencia necesarias para gestionar
 * módulos funcionales, incluyendo consulta por identificador, listado
 * de módulos activos, creación/actualización y verificación de
 * unicidad por nombre.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ModuleRepositoryPort {

    Optional<Module> findById(Long id);

    List<Module> findAllActive();

    Module save(Module module);

    boolean existsByNombre(String nombre);
}
