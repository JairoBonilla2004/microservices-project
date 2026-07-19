package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud de inicio de sesión.
 *
 * <p>Contiene las credenciales del usuario para autenticarse en el sistema.</p>
 *
 * @param username nombre de usuario
 * @param password contraseña del usuario
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record LoginRequest(@NotBlank String username, @NotBlank String password) {
}
