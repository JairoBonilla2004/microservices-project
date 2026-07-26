package ec.edu.espe.master_gateway.contexts.auth.domain.port.out;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

    private final UUID userId;
    private final UUID roleId;
    private final String roleName;
    private final String tokenType;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final String issuer;
    private final Set<String> permissions;

    @SuppressWarnings("java:S107")
    public TokenClaims(UUID userId, UUID roleId, String roleName, String tokenType,
                       Instant issuedAt, Instant expiresAt, String issuer,
                       Set<String> permissions) {
        this.userId = Objects.requireNonNull(userId, "userId no puede ser null");
        this.roleId = roleId;
        this.roleName = roleName;
        this.tokenType = Objects.requireNonNull(tokenType, "tokenType no puede ser null");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt no puede ser null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt no puede ser null");
        this.issuer = issuer;
        this.permissions = permissions != null ? Collections.unmodifiableSet(permissions) : Collections.emptySet();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<String> getPermissions() {
        return permissions;
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