package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;

/**
 * Caso de uso para la creación de un nuevo usuario en el sistema.
 *
 * <p>Este caso de uso orquesta la validación de unicidad del nombre de
 * usuario y correo electrónico, el hasheo de la contraseña y la
 * persistencia del usuario a través de los puertos de salida
 * correspondientes.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface CreateUserUseCase {
    /**
     * Ejecuta la creación de un nuevo usuario.
     *
     * @param request datos del usuario a crear
     * @return respuesta con la información del usuario creado
     */
    CreateUserResponse execute(CreateUserRequest request);
}
