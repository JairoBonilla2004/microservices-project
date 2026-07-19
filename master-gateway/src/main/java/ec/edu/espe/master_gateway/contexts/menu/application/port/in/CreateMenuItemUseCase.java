package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.CreateMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;

/**
 * Caso de uso para la creación de un nuevo elemento de menú.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface CreateMenuItemUseCase {

    /**
     * Crea un nuevo elemento de menú a partir de los datos proporcionados.
     *
     * @param request datos del nuevo elemento de menú.
     * @return respuesta con los datos del elemento creado.
     */
    MenuItemResponse execute(CreateMenuItemRequest request);
}
