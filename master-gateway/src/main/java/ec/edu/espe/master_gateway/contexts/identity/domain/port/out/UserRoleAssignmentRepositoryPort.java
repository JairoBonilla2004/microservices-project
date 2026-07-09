package ec.edu.espe.master_gateway.contexts.identity.domain.port.out;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import java.util.List;
import java.util.Optional;

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

    List<UserRoleAssignment> findByUserId(Long userId);

    List<UserRoleAssignment> findByRoleId(Long roleId);

    Optional<UserRoleAssignment> findByUserIdAndRoleId(Long userId, Long roleId);

    UserRoleAssignment save(UserRoleAssignment assignment);

    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}