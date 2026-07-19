package ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Solicitud para la creación de un nuevo elemento de menú.
 *
 * @param nombre  Nombre visible del elemento de menú (entre 2 y 100 caracteres).
 * @param url     Ruta opcional a la que apunta el elemento.
 * @param moduleId Identificador del módulo al que pertenece el elemento.
 * @param parentId Identificador del elemento padre ({@code null} si es raíz).
 * @param orden   Posición relativa del elemento dentro de su nivel jerárquico.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record CreateMenuItemRequest(
    @NotBlank @Size(min = 2, max = 100) String nombre,
    String url,
    @NotNull UUID moduleId,
    UUID parentId,
    @NotNull Integer orden
) {}
