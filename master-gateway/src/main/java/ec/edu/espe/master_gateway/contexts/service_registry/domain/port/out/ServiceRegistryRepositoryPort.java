package ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out;

import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad {@link RegisteredService}.
 *
 * <p>Define las operaciones de persistencia para el registro de
 * microservicios, permitiendo consultar por código, listar servicios
 * activos, crear/actualizar registros y verificar unicidad del código.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ServiceRegistryRepositoryPort {

    Optional<RegisteredService> findByServiceCode(String serviceCode);

    List<RegisteredService> findAllActive();

    RegisteredService save(RegisteredService service);

    boolean existsByServiceCode(String serviceCode);

    void deleteByServiceCode(String serviceCode);
}
