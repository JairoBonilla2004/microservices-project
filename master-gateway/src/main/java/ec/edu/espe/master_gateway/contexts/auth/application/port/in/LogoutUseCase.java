package ec.edu.espe.master_gateway.contexts.auth.application.port.in;

/**
 * Caso de uso para el cierre de sesión.
 *
 * <p>Invalida el token de actualización proporcionado, impidiendo su
 * uso futuro para renovar tokens de acceso.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface LogoutUseCase {
    /**
     * Ejecuta el cierre de sesión.
     *
     * @param refreshToken token de actualización a invalidar
     * @param accessToken  token de acceso a invalidar (puede ser {@code null})
     */
    void execute(String refreshToken, String accessToken);
}
