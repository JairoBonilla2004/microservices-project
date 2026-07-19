package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Respuesta devuelta tras la creación exitosa de un usuario.
 *
 * <p>Incluye los datos esenciales del usuario recién registrado, como
 * su identificador único, credenciales resumidas y la fecha de creación.
 * Esta respuesta es retornada por el caso de uso {@code CreateUserUseCase}.</p>
 *
 * @param id             identificador único del usuario creado
 * @param username       nombre de usuario asignado
 * @param email          dirección de correo electrónico registrada
 * @param nombreCompleto nombre completo del usuario
 * @param fechaCreacion  fecha y hora en que se creó el usuario
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record CreateUserResponse(
    UUID id,
    String username,
    String email,
    String nombreCompleto,
    LocalDateTime fechaCreacion
) {}
