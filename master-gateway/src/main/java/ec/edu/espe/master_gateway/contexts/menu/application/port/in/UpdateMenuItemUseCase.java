package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.UpdateMenuItemRequest;
import java.util.UUID;

/**
 * Caso de uso para la actualización de un elemento de menú existente.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UpdateMenuItemUseCase {

    /**
     * Actualiza los datos de un elemento de menú identificado por su id.
     *
     * @param id      identificador del elemento a actualizar.
     * @param request datos opcionales a modificar.
     * @return respuesta con los datos del elemento actualizado.
     */
    MenuItemResponse execute(UUID id, UpdateMenuItemRequest request);
}
