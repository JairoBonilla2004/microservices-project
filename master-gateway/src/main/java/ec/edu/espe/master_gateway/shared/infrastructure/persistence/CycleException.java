package ec.edu.espe.master_gateway.shared.infrastructure.persistence;

import ec.edu.espe.master_gateway.shared.domain.DomainException;

/**
 * Excepción lanzada cuando se detecta un ciclo en una estructura jerárquica.
 *
 * <p>Se utiliza principalmente en la validación de árboles de menú para
 * evitar que un nodo se convierta en ancestro de sí mismo, garantizando
 * la integridad de la estructura jerárquica.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class CycleException extends DomainException {

    public CycleException(String mensaje) {
        super(mensaje, "CYCLE_DETECTED");
    }
}
