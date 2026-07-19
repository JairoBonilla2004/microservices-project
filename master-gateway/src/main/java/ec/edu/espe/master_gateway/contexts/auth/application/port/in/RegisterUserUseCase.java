package ec.edu.espe.master_gateway.contexts.auth.application.port.in;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserResponse;

/**
 * Caso de uso para el registro de nuevos usuarios.
 *
 * <p>Valida los datos del usuario, verifica que no exista un usuario con
 * el mismo nombre o correo, y crea la cuenta en el sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RegisterUserUseCase {

    RegisterUserResponse execute(RegisterUserRequest request);
}
