package ec.edu.espe.master_gateway.contexts.module.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para la creación de un nuevo módulo en el sistema.
 *
 * @param nombre      nombre del módulo (obligatorio, entre 2 y 100 caracteres)
 * @param descripcion descripción opcional del módulo
 * @param icono       identificador del icono asociado al módulo (obligatorio)
 * @param orden       posición de visualización del módulo (obligatorio)
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record CreateModuleRequest(
    @NotBlank @Size(min = 2, max = 100) String nombre,
    String descripcion,
    @Size(max = 50) String icono,
    @NotNull Integer orden
) {}
