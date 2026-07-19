package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

/**
 * Respuesta de actualización de token.
 *
 * <p>Contiene el nuevo par de tokens de acceso y actualización generados
 * tras una renovación exitosa.</p>
 *
 * @param accessToken  nuevo token de acceso JWT
 * @param refreshToken nuevo token de actualización JWT
 * @param expiresIn    tiempo de expiración del token de acceso en segundos
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RefreshTokenResponse(String accessToken, String refreshToken, Long expiresIn) {
}
