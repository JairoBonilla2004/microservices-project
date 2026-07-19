package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.UpdateModuleRequest;
import java.util.UUID;

/**
 * Caso de uso para la actualización de un módulo existente.
 *
 * <p>Busca el módulo por su identificador y aplica los cambios
 * proporcionados en la solicitud.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UpdateModuleUseCase {

    /**
     * Ejecuta la actualización de un módulo.
     *
     * @param id      identificador del módulo a actualizar
     * @param request datos opcionales con los nuevos valores
     * @return respuesta con la información actualizada del módulo
     */
    ModuleResponse execute(UUID id, UpdateModuleRequest request);
}
