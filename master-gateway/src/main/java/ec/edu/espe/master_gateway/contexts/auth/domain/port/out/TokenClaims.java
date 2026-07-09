package ec.edu.espe.master_gateway.contexts.auth.domain.port.out;

import java.time.Instant;
import java.util.Objects;

/**
 * Representa la información contenida en las reclamaciones (Claims) de un
 * token JWT.
 *
 * <p>Encapsula los datos utilizados durante los procesos de generación y
 * validación de tokens, incluyendo la identidad del usuario, el rol
 * asociado, el tipo de token, las fechas de emisión y expiración, así como
 * el emisor del token. Esta información permite verificar la autenticidad
 * y vigencia de los JWT dentro del dominio de autenticación.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class TokenClaims {

    private final Long userId;
    private final Long roleId;
    private final String tokenType;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final String issuer;

    public TokenClaims(Long userId, Long roleId, String tokenType,
                       Instant issuedAt, Instant expiresAt, String issuer) {
        this.userId = Objects.requireNonNull(userId, "userId no puede ser null");
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.tokenType = Objects.requireNonNull(tokenType, "tokenType no puede ser null");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt no puede ser null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt no puede ser null");
        this.issuer = issuer;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getIssuer() {
        return issuer;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}