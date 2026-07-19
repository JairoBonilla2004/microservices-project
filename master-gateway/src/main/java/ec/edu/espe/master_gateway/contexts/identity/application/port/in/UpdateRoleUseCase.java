package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateRoleRequest;
import java.util.UUID;

/**
 * Caso de uso para la actualización de un rol existente.
 *
 * <p>Permite modificar el nombre y la descripción de un rol previamente
 * creado. Valida la unicidad del nombre en caso de que se intente
 * cambiar.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UpdateRoleUseCase {
    /**
     * Ejecuta la actualización de un rol.
     *
     * @param id      identificador único del rol a actualizar
     * @param request datos con los campos a modificar
     * @return respuesta con la información del rol actualizado
     */
    RoleResponse execute(UUID id, UpdateRoleRequest request);
}
