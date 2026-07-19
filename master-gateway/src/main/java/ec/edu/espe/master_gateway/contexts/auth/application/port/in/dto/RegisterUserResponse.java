package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import java.util.UUID;

/**
 * Respuesta de registro de usuario.
 *
 * <p>Contiene los datos del usuario recién creado en el sistema,
 * excluyendo información sensible como la contraseña.</p>
 *
 * @param id             identificador único del usuario
 * @param username       nombre de usuario
 * @param email          correo electrónico del usuario
 * @param nombreCompleto nombre completo del usuario
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RegisterUserResponse(
    UUID id,
    String username,
    String email,
    String nombreCompleto
) {}
