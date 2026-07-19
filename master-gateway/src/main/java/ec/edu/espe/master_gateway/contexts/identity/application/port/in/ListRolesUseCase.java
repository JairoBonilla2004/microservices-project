package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import java.util.List;

/**
 * Caso de uso para el listado de todos los roles del sistema.
 *
 * <p>Retorna una lista con todos los roles registrados, incluyendo
 * tanto los activos como los inactivos. Puede ser filtrada o paginada
 * en implementaciones posteriores.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ListRolesUseCase {
    /**
     * Ejecuta el listado de roles.
     *
     * @return lista de respuestas con la información de todos los roles
     */
    List<RoleResponse> execute();
}
