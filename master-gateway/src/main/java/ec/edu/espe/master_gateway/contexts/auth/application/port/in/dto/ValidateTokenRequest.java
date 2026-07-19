package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud de validación de token.
 *
 * <p>Contiene el token JWT a validar y, opcionalmente, el código del
 * servicio que solicita la validación para aplicar reglas específicas.</p>
 *
 * @param token       token JWT a validar
 * @param serviceCode código del servicio solicitante (opcional)
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record ValidateTokenRequest(@NotBlank String token, String serviceCode) {
}
