package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import java.util.List;

/**
 * Caso de uso para la consulta de servicios registrados activos.
 *
 * <p>Define la operaci&oacute;n que recupera todos los microservicios cuyo
 * estado es {@code ACTIVE} en el registro de servicios.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface ListRegisteredServicesUseCase {

    /**
     * Ejecuta la consulta de servicios activos.
     *
     * @return lista de servicios activos registrados en el sistema.
     */
    List<ServiceResponse> execute();
}
