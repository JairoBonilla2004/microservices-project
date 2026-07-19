package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Solicitud de selección de rol.
 *
 * <p>Contiene el token temporal y el identificador del rol que el usuario
 * desea seleccionar para continuar con la autenticación.</p>
 *
 * @param tempToken token temporal emitido durante el inicio de sesión
 * @param roleId    identificador del rol a seleccionar
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record SelectRoleRequest(@NotBlank String tempToken, @NotNull UUID roleId) {
}
