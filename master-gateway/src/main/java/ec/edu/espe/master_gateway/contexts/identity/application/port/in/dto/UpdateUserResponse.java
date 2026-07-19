package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Respuesta devuelta tras la actualización exitosa de un usuario.
 *
 * <p>Contiene los datos del usuario después de aplicar las modificaciones,
 * incluyendo el estado actualizado y la fecha de la última modificación.
 * Es retornada por el caso de uso {@code UpdateUserUseCase}.</p>
 *
 * @param id                 identificador único del usuario actualizado
 * @param username           nombre de usuario
 * @param email              dirección de correo electrónico actualizada
 * @param nombreCompleto     nombre completo del usuario
 * @param estado             estado actual del usuario (ACTIVO/INACTIVO)
 * @param fechaActualizacion fecha y hora de la última actualización
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UpdateUserResponse(
    UUID id,
    String username,
    String email,
    String nombreCompleto,
    String estado,
    LocalDateTime fechaActualizacion
) {}
