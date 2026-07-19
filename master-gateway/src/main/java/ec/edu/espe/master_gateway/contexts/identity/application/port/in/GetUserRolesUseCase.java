package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para la consulta de los roles asignados a un usuario.
 *
 * <p>Recupera todos los roles asociados a un usuario específico.
 * Si el usuario no existe, debe lanzar una excepción de tipo
 * {@code ResourceNotFoundException}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetUserRolesUseCase {
    /**
     * Ejecuta la consulta de roles de un usuario.
     *
     * @param userId identificador único del usuario
     * @return lista de roles asignados al usuario
     */
    List<RoleResponse> execute(UUID userId);
}
