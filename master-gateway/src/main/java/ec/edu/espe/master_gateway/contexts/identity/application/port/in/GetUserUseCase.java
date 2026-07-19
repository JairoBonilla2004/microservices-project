package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import java.util.UUID;

/**
 * Caso de uso para la consulta de un usuario por su identificador.
 *
 * <p>Recupera la información completa de un usuario a partir de su ID.
 * Si el usuario no existe, debe lanzar una excepción de tipo
 * {@code ResourceNotFoundException}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetUserUseCase {
    /**
     * Ejecuta la consulta de un usuario.
     *
     * @param id identificador único del usuario a consultar
     * @return respuesta con la información completa del usuario
     */
    UserResponse execute(UUID id);
}
