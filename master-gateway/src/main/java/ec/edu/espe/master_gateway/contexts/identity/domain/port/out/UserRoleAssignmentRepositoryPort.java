package ec.edu.espe.master_gateway.contexts.identity.domain.port.out;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida del dominio para la persistencia de asignaciones entre
 * usuarios y roles.
 *
 * <p>Define las operaciones necesarias para gestionar la relación entre
 * usuarios y roles dentro del sistema, permitiendo consultar, registrar y
 * eliminar asignaciones. Su implementación corresponde a la capa de
 * infraestructura, manteniendo el dominio desacoplado de la tecnología de
 * persistencia y siguiendo los principios de la Arquitectura Hexagonal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UserRoleAssignmentRepositoryPort {

    List<UserRoleAssignment> findByUserId(UUID userId);

    List<UserRoleAssignment> findByRoleId(UUID roleId);

    Optional<UserRoleAssignment> findByUserIdAndRoleId(UUID userId, UUID roleId);

    UserRoleAssignment save(UserRoleAssignment assignment);
}