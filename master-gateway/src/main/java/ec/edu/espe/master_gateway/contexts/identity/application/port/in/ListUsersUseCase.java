package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import java.util.List;

/**
 * Caso de uso para el listado de todos los usuarios del sistema.
 *
 * <p>Retorna una lista con todos los usuarios registrados, incluyendo
 * tanto los activos como los inactivos. Puede ser filtrada o paginada
 * en implementaciones posteriores.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ListUsersUseCase {
    /**
     * Ejecuta el listado de usuarios.
     *
     * @return lista de respuestas con la información de todos los usuarios
     */
    List<UserResponse> execute();
}
