package ec.edu.espe.master_gateway.contexts.auth.application.port.in;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.TokenValidationResponse;

/**
 * Caso de uso para la validación de tokens JWT.
 *
 * <p>Verifica la integridad, vigencia y tipo de un token JWT, retornando
 * la información asociada al mismo si es válido. La validación puede
 * hacerme contra el modo de validación específico de un servicio
 * registrado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ValidateTokenUseCase {
    /**
     * Ejecuta la validación del token.
     *
     * @param token       token JWT a validar
     * @param serviceCode código del servicio que solicita la validación
     * @return respuesta indicando si el token es válido y sus datos asociados
     */
    TokenValidationResponse execute(String token, String serviceCode);
}
