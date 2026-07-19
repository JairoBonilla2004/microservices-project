package ec.edu.espe.master_gateway.contexts.auth.application.port.in;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginResponse;

/**
 * Caso de uso para la autenticación de usuarios.
 *
 * <p>Valida las credenciales, verifica roles activos y emite un token
 * temporal de selección de rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface LoginUseCase {
    /**
     * Ejecuta el inicio de sesión.
     *
     * @param request credenciales del usuario
     * @return respuesta con token temporal y lista de roles disponibles
     */
    LoginResponse execute(LoginRequest request);
}
