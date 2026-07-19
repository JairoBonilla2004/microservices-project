package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleResponse;

/**
 * Caso de uso para la creación de un nuevo rol en el sistema.
 *
 * <p>Este caso de uso orquesta la validación de unicidad del nombre
 * del rol y la persistencia del mismo a través de los puertos de
 * salida correspondientes.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface CreateRoleUseCase {
    /**
     * Ejecuta la creación de un nuevo rol.
     *
     * @param request datos del rol a crear
     * @return respuesta con la información del rol creado
     */
    CreateRoleResponse execute(CreateRoleRequest request);
}
