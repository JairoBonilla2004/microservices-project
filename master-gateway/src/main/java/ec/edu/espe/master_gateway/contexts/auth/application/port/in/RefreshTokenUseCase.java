package ec.edu.espe.master_gateway.contexts.auth.application.port.in;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenResponse;

/**
 * Caso de uso para la renovación de tokens.
 *
 * <p>Valida el token de actualización, lo revoca y emite un nuevo par
 * de tokens de acceso y actualización.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RefreshTokenUseCase {
    /**
     * Ejecuta la renovación de tokens.
     *
     * @param request token de actualización actual
     * @return respuesta con nuevo par de tokens
     * @throws AuthenticationException si el token de actualización es inválido
     */
    RefreshTokenResponse execute(RefreshTokenRequest request);
}
