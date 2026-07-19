package ec.edu.espe.master_gateway.contexts.module.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Respuesta devuelta tras la creación exitosa de un módulo.
 *
 * @param id            identificador único del módulo creado
 * @param nombre        nombre del módulo
 * @param descripcion   descripción del módulo
 * @param icono         icono asociado al módulo
 * @param orden         posición de visualización del módulo
 * @param fechaCreacion fecha y hora en que se creó el módulo
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record CreateModuleResponse(
    UUID id,
    String nombre,
    String descripcion,
    String icono,
    int orden,
    LocalDateTime fechaCreacion
) {}
