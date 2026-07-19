package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para la consulta de los usuarios que tienen asignado un rol.
 *
 * <p>Recupera todos los usuarios que poseen un rol específico.
 * Si el rol no existe, debe lanzar una excepción de tipo
 * {@code ResourceNotFoundException}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetRoleUsersUseCase {
    /**
     * Ejecuta la consulta de usuarios por rol.
     *
     * @param roleId identificador único del rol
     * @return lista de usuarios que tienen asignado el rol
     */
    List<UserResponse> execute(UUID roleId);
}
