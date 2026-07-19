package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleRequest;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleResponse;

/**
 * Caso de uso para la creación de un nuevo módulo en el sistema.
 *
 * <p>Valida que no exista un módulo con el mismo nombre antes de
 * persistir la entidad.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface CreateModuleUseCase {

    /**
     * Ejecuta la creación de un módulo a partir de los datos de la solicitud.
     *
     * @param request datos necesarios para la creación del módulo
     * @return respuesta con la información del módulo creado
     */
    CreateModuleResponse execute(CreateModuleRequest request);
}
