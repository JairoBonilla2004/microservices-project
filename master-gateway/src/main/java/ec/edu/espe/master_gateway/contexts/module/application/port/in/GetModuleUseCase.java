package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import java.util.UUID;

/**
 * Caso de uso para la obtención de un módulo por su identificador.
 *
 * <p>Recupera los datos completos de un módulo específico del sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetModuleUseCase {
    ModuleResponse execute(UUID id);
}
