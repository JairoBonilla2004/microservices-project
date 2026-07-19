package ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto;

import jakarta.validation.constraints.Size;

/**
 * Solicitud para la actualización de un elemento de menú existente.
 *
 * <p>Todos los campos son opcionales; solo se actualizarán los que
 * no sean {@code null}.</p>
 *
 * @param nombre Nuevo nombre del elemento (entre 2 y 100 caracteres).
 * @param url    Nueva ruta del elemento.
 * @param orden  Nueva posición relativa del elemento.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UpdateMenuItemRequest(
    @Size(min = 2, max = 100) String nombre,
    String url,
    Integer orden
) {}
