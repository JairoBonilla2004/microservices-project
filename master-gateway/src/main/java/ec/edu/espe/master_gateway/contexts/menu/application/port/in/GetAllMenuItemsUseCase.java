package ec.edu.espe.master_gateway.contexts.menu.application.port.in;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import java.util.List;

/**
 * Caso de uso para obtener el listado completo de ítems de menú activos,
 * sin filtrar por rol ni módulo.
 *
 * <p>Sirve como catálogo administrativo: permite, por ejemplo, elegir un
 * ítem existente para asignarlo a un rol, independientemente de si dicho
 * rol ya lo tiene o no.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetAllMenuItemsUseCase {

    /**
     * Recupera todos los ítems de menú activos del sistema, en forma plana
     * (sin anidar hijos), ordenados por el campo {@code orden}.
     *
     * @return lista de ítems de menú activos.
     */
    List<MenuItemResponse> execute();
}
