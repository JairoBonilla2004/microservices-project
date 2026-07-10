package ec.edu.espe.master_gateway.shared.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Excepción base del dominio para todas las excepciones de la aplicación.
 *
 * <p>Proporciona una estructura estandarizada con un código de error y un
 * mapa de detalles adicionales que permiten transportar información
 * contextual sobre el error ocurrido. Todas las excepciones específicas
 * del negocio deben extender esta clase.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public abstract class DomainException extends RuntimeException {

    private final String codigoError;
    private final Map<String, Object> detalles;

    protected DomainException(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
        this.detalles = new HashMap<>();
    }

    public String getCodigoError() {
        return codigoError;
    }

    public Map<String, Object> getDetalles() {
        return Map.copyOf(detalles);
    }

    protected void addDetalle(String clave, Object valor) {
        this.detalles.put(clave, valor);
    }
}
