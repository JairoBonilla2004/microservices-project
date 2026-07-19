package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud de actualización de token.
 *
 * <p>Contiene el token de actualización para obtener un nuevo par de
 * tokens de acceso y actualización.</p>
 *
 * @param refreshToken token de actualización JWT
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RefreshTokenRequest(@NotBlank String refreshToken) {
}
