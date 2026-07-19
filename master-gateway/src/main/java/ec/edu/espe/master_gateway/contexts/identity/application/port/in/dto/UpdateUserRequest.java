package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para la actualización de los datos de un usuario existente.
 *
 * <p>Permite modificar campos como el correo electrónico, el nombre
 * completo y la contraseña del usuario. Los campos opcionales solo se
 * actualizan si se proporcionan. La contraseña actual debe enviarse
 * para autorizar el cambio de contraseña.</p>
 *
 * @param email           nuevo correo electrónico del usuario (opcional)
 * @param nombreCompleto  nuevo nombre completo del usuario (opcional)
 * @param currentPassword contraseña actual para verificar la identidad
 * @param newPassword     nueva contraseña, debe tener al menos 8 caracteres (opcional)
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UpdateUserRequest(
    @Email String email,
    String nombreCompleto,
    String currentPassword,
    @Size(min = 8) String newPassword
) {}
