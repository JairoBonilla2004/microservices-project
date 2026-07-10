package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando un usuario no tiene permisos para una acción.
 *
 * <p>Indica que el usuario autenticado carece de los permisos necesarios
 * para ejecutar la operación solicitada, utilizando el código de error
 * {@code FORBIDDEN}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class AuthorizationException extends DomainException {

    public AuthorizationException(String mensaje) {
        super(mensaje, "FORBIDDEN");
    }
}
