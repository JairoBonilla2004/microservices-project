package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import java.util.UUID;

/**
 * Respuesta de validación de token.
 *
 * <p>Indica si un token JWT es válido y, en caso afirmativo, proporciona
 * la información asociada al mismo.</p>
 *
 * @param valid     indica si el token es válido
 * @param userId    identificador del usuario asociado al token
 * @param roleId    identificador del rol asociado al token
 * @param tokenType tipo de token (acceso, actualización o temporal)
 * @param message   mensaje descriptivo en caso de error
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record TokenValidationResponse(boolean valid, UUID userId, UUID roleId, String tokenType, String message) {
}
