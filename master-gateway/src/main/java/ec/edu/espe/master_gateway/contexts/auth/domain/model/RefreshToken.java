package ec.edu.espe.master_gateway.contexts.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un token de actualización (Refresh Token) asociado a un usuario.
 *
 * <p>Un refresh token permite solicitar la emisión de un nuevo token de acceso
 * cuando este ha expirado, sin que el usuario deba autenticarse nuevamente.
 * Cada token está vinculado a un usuario, al rol con el que inició sesión y
 * posee un período de validez determinado.</p>
 *
 * <p>La entidad mantiene el estado del token, permitiendo verificar si continúa
 * siendo válido, si ha expirado o si fue revocado como parte del proceso de
 * cierre de sesión o por razones de seguridad.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class RefreshToken {

    /**
     * Identificador único del refresh token.
     */
    private Long id;

    /**
     * Valor del refresh token.
     */
    private final String token;

    /**
     * Identificador del usuario propietario del token.
     */
    private final Long userId;

    /**
     * Identificador del rol con el que fue emitido el token.
     */
    private final Long roleId;

    /**
     * Fecha y hora de expiración del token.
     */
    private final LocalDateTime expiresAt;

    /**
     * Indica si el token ha sido revocado.
     */
    private boolean revoked;

    /**
     * Fecha y hora de creación del token.
     */
    private final LocalDateTime createdAt;

    /**
     * Crea un nuevo refresh token.
     *
     * @param token valor único del refresh token.
     * @param userId identificador del usuario propietario.
     * @param roleId identificador del rol asociado al token.
     * @param expiresAt fecha y hora en la que el token dejará de ser válido.
     * @throws NullPointerException si alguno de los parámetros obligatorios es
     *                              nulo.
     */
    public RefreshToken(String token, Long userId, Long roleId, LocalDateTime expiresAt) {
        this.token = Objects.requireNonNull(token, "token no puede ser null");
        this.userId = Objects.requireNonNull(userId, "userId no puede ser null");
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt no puede ser null");
        this.revoked = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Revoca el token, impidiendo su utilización en futuras solicitudes de
     * renovación de acceso.
     */
    public void revoke() {
        this.revoked = true;
    }

    /**
     * Verifica si el token puede utilizarse para solicitar un nuevo token
     * de acceso.
     *
     * <p>Un token es considerado válido cuando no ha sido revocado y su fecha
     * de expiración aún no ha sido alcanzada.</p>
     *
     * @return {@code true} si el token continúa siendo válido; caso contrario,
     *         {@code false}.
     */
    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @return {@code true} si la fecha actual es posterior a la fecha de
     *         expiración; en caso contrario, {@code false}.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Obtiene el identificador del token.
     *
     * @return identificador del refresh token.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el valor del refresh token.
     *
     * @return cadena que representa el token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Obtiene el identificador del usuario propietario.
     *
     * @return identificador del usuario.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Obtiene el identificador del rol asociado al token.
     *
     * @return identificador del rol.
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Obtiene la fecha y hora de expiración del token.
     *
     * @return fecha de expiración.
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Indica si el token ha sido revocado.
     *
     * @return {@code true} si el token fue revocado; caso contrario,
     *         {@code false}.
     */
    public boolean isRevoked() {
        return revoked;
    }

    /**
     * Obtiene la fecha y hora de creación del token.
     *
     * @return fecha de creación del token.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}