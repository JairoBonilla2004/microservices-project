package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando los datos de entrada no son válidos.
 *
 * <p>Indica que los parámetros proporcionados por el cliente no superan
 * las validaciones de formato, tipo o rango, utilizando el código de
 * error {@code VALIDATION_ERROR}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class InvalidInputException extends DomainException {

    public InvalidInputException(String mensaje) {
        super(mensaje, "VALIDATION_ERROR");
    }
}
