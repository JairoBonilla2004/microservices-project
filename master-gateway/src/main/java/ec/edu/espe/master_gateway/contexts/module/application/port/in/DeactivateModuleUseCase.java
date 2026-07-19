package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para la desactivación de un módulo.
 *
 * <p>Cambia el estado del módulo a inactivo para que deje de
 * estar disponible en el sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface DeactivateModuleUseCase {

    /**
     * Ejecuta la desactivación del módulo con el identificador dado.
     *
     * @param id identificador del módulo que se desea desactivar
     */
    void execute(UUID id);
}
