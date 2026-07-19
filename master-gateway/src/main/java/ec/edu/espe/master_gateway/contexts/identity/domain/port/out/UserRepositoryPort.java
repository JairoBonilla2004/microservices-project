package ec.edu.espe.master_gateway.contexts.identity.domain.port.out;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida del dominio para la persistencia de usuarios.
 *
 * <p>Define las operaciones requeridas por el dominio para consultar,
 * almacenar y verificar la existencia de usuarios dentro del sistema.
 * Su implementación corresponde a la capa de infraestructura, permitiendo
 * desacoplar la lógica de negocio de la tecnología de persistencia
 * utilizada y favoreciendo la aplicación de los principios de la
 * Arquitectura Hexagonal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UserRepositoryPort {

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    List<User> findAllActive();

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}