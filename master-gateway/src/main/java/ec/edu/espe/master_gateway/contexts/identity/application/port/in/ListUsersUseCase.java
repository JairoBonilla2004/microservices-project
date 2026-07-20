package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.shared.domain.PageResult;

/**
 * Caso de uso para el listado paginado de usuarios activos del sistema.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ListUsersUseCase {
    /**
     * Ejecuta el listado paginado de usuarios activos.
     *
     * @param page número de página (0-indexado).
     * @param size tamaño de página.
     * @return página de respuestas con la información de los usuarios activos.
     */
    PageResult<UserResponse> execute(int page, int size);
}
