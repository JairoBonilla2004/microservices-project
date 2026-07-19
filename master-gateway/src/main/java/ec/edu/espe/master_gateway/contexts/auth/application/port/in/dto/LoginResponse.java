package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import java.util.List;

/**
 * Respuesta de inicio de sesión.
 *
 * <p>Contiene el token temporal y la lista de roles disponibles del usuario
 * autenticado.</p>
 *
 * @param tempToken token temporal para la selección de rol
 * @param roles     lista de roles activos del usuario
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record LoginResponse(String tempToken, List<RoleInfo> roles) {
}
