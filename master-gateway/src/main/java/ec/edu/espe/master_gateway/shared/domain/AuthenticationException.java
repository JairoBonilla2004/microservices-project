package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando falla la autenticación de un usuario.
 *
 * <p>Indica que las credenciales proporcionadas son inválidas o que el
 * token de acceso ha expirado, utilizando el código {@code AUTH_FAILED}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class AuthenticationException extends DomainException {

    public AuthenticationException(String mensaje) {
        super(mensaje, "AUTH_FAILED");
    }
}
