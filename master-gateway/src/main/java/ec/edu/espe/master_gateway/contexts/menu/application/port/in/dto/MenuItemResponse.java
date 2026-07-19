package ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto;

import java.util.UUID;

/**
 * Respuesta con los datos de un elemento de menú.
 *
 * @param id       Identificador único del elemento de menú.
 * @param nombre   Nombre visible del elemento.
 * @param url      Ruta a la que apunta el elemento.
 * @param moduleId Identificador del módulo al que pertenece.
 * @param parentId Identificador del elemento padre ({@code null} si es raíz).
 * @param orden    Posición relativa del elemento en su nivel jerárquico.
 * @param estado   Estado actual del elemento (ACTIVO o INACTIVO).
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record MenuItemResponse(
    UUID id,
    String nombre,
    String url,
    UUID moduleId,
    UUID parentId,
    int orden,
    String estado
) {}
