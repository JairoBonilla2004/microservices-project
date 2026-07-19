package ec.edu.espe.master_gateway.contexts.module.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representación completa de un módulo para su uso en respuestas de la API.
 *
 * @param id            identificador único del módulo
 * @param nombre        nombre del módulo
 * @param descripcion   descripción del módulo
 * @param icono         icono asociado al módulo
 * @param orden         posición de visualización del módulo
 * @param estado        estado actual del módulo (ACTIVE, INACTIVE)
 * @param fechaCreacion fecha y hora de creación del módulo
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record ModuleResponse(
    UUID id,
    String nombre,
    String descripcion,
    String icono,
    int orden,
    String estado,
    LocalDateTime fechaCreacion
) {}
