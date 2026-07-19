package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserResponse;
import java.util.UUID;

/**
 * Caso de uso para la actualización de los datos de un usuario existente.
 *
 * <p>Este caso de uso permite modificar información personal y credenciales
 * de un usuario previamente registrado. Valida la identidad del usuario
 * mediante la contraseña actual antes de aplicar los cambios.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UpdateUserUseCase {
    /**
     * Ejecuta la actualización de un usuario.
     *
     * @param id      identificador único del usuario a actualizar
     * @param request datos con los campos a modificar
     * @return respuesta con la información del usuario actualizado
     */
    UpdateUserResponse execute(UUID id, UpdateUserRequest request);
}
