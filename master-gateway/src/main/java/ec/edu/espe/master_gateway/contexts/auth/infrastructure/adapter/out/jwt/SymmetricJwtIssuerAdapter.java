package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

/**
 * Adaptador de emisión de tokens JWT utilizando el algoritmo simétrico HMAC-SHA256.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort}
 * utilizando la biblioteca jjwt ({@code io.jsonwebtoken}) para construir y firmar
 * los tokens con una clave secreta compartida. Soporta tres tipos de tokens:
 * temporales ({@code TEMP_TOKEN}), de acceso ({@code ACCESS_TOKEN}) y de
 * actualización ({@code REFRESH_TOKEN}).</p>
 *
 * <p>La clave secreta se obtiene de {@link JwtProperties} y debe tener una
 * longitud mínima de 256 bits (32 caracteres) para HMAC-SHA256. Los tiempos
 * de expiración se configuran en el archivo {@code application.yml} bajo el
 * prefijo {@code jwt}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jwt.mode", havingValue = "direct", matchIfMissing = true)
public class SymmetricJwtIssuerAdapter implements TokenIssuerPort {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public SymmetricJwtIssuerAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String issueTempToken(UUID userId) {
        return buildToken(userId, null, "TEMP_TOKEN", jwtProperties.getTempTokenExpiration());
    }

    @Override
    public String issueAccessToken(UUID userId, UUID roleId, Set<String> permissions, String roleName, String username) {
        var now = Instant.now();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", "ACCESS_TOKEN")
                .claim("roleName", roleName)
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.getAccessTokenExpiration())))
                .issuer("master-gateway")
                .signWith(secretKey, Jwts.SIG.HS256);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        if (permissions != null && !permissions.isEmpty()) {
            builder.claim("permissions", String.join(",", permissions));
        }
        return builder.compact();
    }

    @Override
    public String issueRefreshToken(UUID userId, UUID roleId, String roleName) {
        var now = Instant.now();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", "REFRESH_TOKEN")
                .claim("roleName", roleName)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.getRefreshTokenExpiration())))
                .issuer("master-gateway")
                .signWith(secretKey, Jwts.SIG.HS256);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        return builder.compact();
    }

    private String buildToken(UUID userId, UUID roleId, String type, java.time.Duration expiration) {
        var now = Instant.now();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .issuer("master-gateway")
                .signWith(secretKey, Jwts.SIG.HS256);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        return builder.compact();
    }
}
