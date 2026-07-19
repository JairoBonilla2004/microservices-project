package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representación completa de un rol para su consulta.
 *
 * <p>Incluye todos los datos relevantes del rol, como su identificador,
 * nombre, descripción, estado y fecha de creación. Es utilizada como
 * respuesta en los casos de uso de consulta y listado de roles.</p>
 *
 * @param id            identificador único del rol
 * @param nombre        nombre del rol
 * @param descripcion   descripción del rol
 * @param estado        estado actual del rol (ACTIVO/INACTIVO)
 * @param fechaCreacion fecha y hora de creación del rol
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RoleResponse(
    UUID id,
    String nombre,
    String descripcion,
    String estado,
    LocalDateTime fechaCreacion
) {}
