package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import java.util.List;

/**
 * Caso de uso para la obtención de todos los módulos activos.
 *
 * <p>Recupera los módulos cuyo estado es activo y los devuelve
 * como una lista de respuestas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ListModulesUseCase {

    /**
     * Ejecuta la consulta de los módulos activos del sistema.
     *
     * @return lista de módulos activos
     */
    List<ModuleResponse> execute();
}
