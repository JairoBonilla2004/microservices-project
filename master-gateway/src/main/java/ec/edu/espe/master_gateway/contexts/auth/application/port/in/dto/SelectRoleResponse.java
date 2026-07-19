package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

/**
 * Respuesta de selección de rol.
 *
 * <p>Contiene los tokens de acceso y actualización generados tras la
 * selección exitosa de un rol.</p>
 *
 * @param accessToken  token de acceso JWT
 * @param refreshToken token de actualización JWT
 * @param expiresIn    tiempo de expiración del token de acceso en segundos
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record SelectRoleResponse(String accessToken, String refreshToken, Long expiresIn) {
}
