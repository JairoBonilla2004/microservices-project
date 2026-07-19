package ec.edu.espe.master_gateway.contexts.module.application.port.in.dto;

import jakarta.validation.constraints.Size;

/**
 * Solicitud para la actualización de un módulo existente.
 *
 * <p>Todos los campos son opcionales; solo se actualizarán los que
 * se proporcionen en la solicitud.</p>
 *
 * @param nombre      nuevo nombre del módulo (entre 2 y 100 caracteres si se especifica)
 * @param descripcion nueva descripción del módulo
 * @param icono       nuevo icono del módulo
 * @param orden       nueva posición de visualización del módulo
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UpdateModuleRequest(
    @Size(min = 2, max = 100) String nombre,
    String descripcion,
    String icono,
    Integer orden
) {}
