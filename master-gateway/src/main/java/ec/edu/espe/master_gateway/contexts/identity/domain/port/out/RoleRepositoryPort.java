package ec.edu.espe.master_gateway.contexts.identity.domain.port.out;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida del dominio para la persistencia de roles.
 *
 * <p>Define las operaciones requeridas por el dominio para consultar,
 * almacenar y verificar la existencia de roles dentro del sistema. Su
 * implementación pertenece a la capa de infraestructura, permitiendo que
 * el dominio permanezca independiente de la tecnología de persistencia
 * utilizada y siguiendo los principios de la Arquitectura Hexagonal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RoleRepositoryPort {

    Optional<Role> findById(UUID id);

    List<Role> findAllActive();

    Role save(Role role);

    boolean existsByNombre(String nombre);
}