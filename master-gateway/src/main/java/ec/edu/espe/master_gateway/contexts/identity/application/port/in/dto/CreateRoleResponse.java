package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Respuesta devuelta tras la creación exitosa de un rol.
 *
 * <p>Incluye los datos esenciales del rol recién creado, como su
 * identificador único, nombre, descripción y fecha de creación.
 * Es retornada por el caso de uso {@code CreateRoleUseCase}.</p>
 *
 * @param id            identificador único del rol creado
 * @param nombre        nombre del rol
 * @param descripcion   descripción del rol
 * @param fechaCreacion fecha y hora de creación del rol
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record CreateRoleResponse(
    UUID id,
    String nombre,
    String descripcion,
    LocalDateTime fechaCreacion
) {}
