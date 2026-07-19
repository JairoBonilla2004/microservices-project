package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.UpdateServiceRequest;

/**
 * Caso de uso para la actualizaci&oacute;n de un servicio existente.
 *
 * <p>Define la operaci&oacute;n de modificaci&oacute;n de los datos de un
 * microservicio previamente registrado, identific&aacute;ndolo por su c&oacute;digo
 * &uacute;nico.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface UpdateServiceUseCase {

    /**
     * Ejecuta la actualizaci&oacute;n de un servicio.
     *
     * @param serviceCode c&oacute;digo &uacute;nico del servicio a actualizar.
     * @param request     solicitud con los campos a modificar.
     * @return respuesta con los datos actualizados del servicio.
     */
    ServiceResponse execute(String serviceCode, UpdateServiceRequest request);
}
