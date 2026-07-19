package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in;

/**
 * Caso de uso para la desactivaci&oacute;n de un servicio registrado.
 *
 * <p>Define la operaci&oacute;n que permite marcar un microservicio como
 * inactivo, identific&aacute;ndolo por su c&oacute;digo &uacute;nico.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface DeactivateServiceUseCase {

    /**
     * Ejecuta la desactivaci&oacute;n de un servicio.
     *
     * @param serviceCode c&oacute;digo &uacute;nico del servicio a desactivar.
     */
    void execute(String serviceCode);
}
