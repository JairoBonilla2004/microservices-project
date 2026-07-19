package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceRequest;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceResponse;

/**
 * Caso de uso para el registro de un nuevo servicio en el sistema.
 *
 * <p>Define la operaci&oacute;n de alta de un microservicio, validando que el
 * c&oacute;digo del servicio no exista previamente y persistiendo los datos
 * proporcionados.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RegisterServiceUseCase {

    /**
     * Ejecuta el registro de un nuevo servicio.
     *
     * @param request solicitud con los datos del servicio a registrar.
     * @return respuesta con los datos del servicio registrado.
     */
    RegisterServiceResponse execute(RegisterServiceRequest request);
}
