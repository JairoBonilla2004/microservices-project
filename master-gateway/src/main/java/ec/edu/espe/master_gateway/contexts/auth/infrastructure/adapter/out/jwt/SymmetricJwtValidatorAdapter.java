package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

/**
 * Adaptador de validación de tokens JWT utilizando el algoritmo simétrico HMAC-SHA256.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort}
 * utilizando jjwt para parsear y verificar la firma de los tokens con la misma
 * clave secreta usada en la emisión. Incluye un mecanismo en memoria
 * ({@link java.util.concurrent.ConcurrentHashMap}) para invalidar tokens
 * temporales que ya han sido utilizados durante el flujo de selección de rol.</p>
 *
 * <p>Valida que el tipo del token coincida con el esperado ({@code ACCESS_TOKEN},
 * {@code REFRESH_TOKEN} o {@code TEMP_TOKEN}) y extrae los claims necesarios
 * para construir un objeto {@link ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jwt.mode", havingValue = "direct", matchIfMissing = true)
public class SymmetricJwtValidatorAdapter implements TokenValidationPort {

    private final SecretKey secretKey;
    private final Set<String> invalidatedTempTokens = ConcurrentHashMap.newKeySet();
    private final Set<String> revokedAccessTokens = ConcurrentHashMap.newKeySet();
    private final RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    public SymmetricJwtValidatorAdapter(JwtProperties jwtProperties,
                                         RevokedTokenRepositoryPort revokedTokenRepositoryPort) {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
        this.revokedTokenRepositoryPort = revokedTokenRepositoryPort;
    }

    @Override
    public TokenClaims validate(String token) {
        if (revokedAccessTokens.contains(token) || revokedTokenRepositoryPort.existsByToken(token)) {
            throw new JwtException("Token de acceso revocado");
        }
        var claims = parseToken(token);
        var tokenType = claims.get("type", String.class);
        if (!"ACCESS_TOKEN".equals(tokenType)) {
            throw new JwtException("Tipo de token invalido: " + tokenType);
        }
        return toTokenClaims(claims);
    }

    @Override
    public TokenClaims validateTempToken(String token) {
        if (invalidatedTempTokens.contains(token)) {
            throw new JwtException("Token temporal ya fue utilizado");
        }
        var claims = parseToken(token);
        var tokenType = claims.get("type", String.class);
        if (!"TEMP_TOKEN".equals(tokenType)) {
            throw new JwtException("Tipo de token invalido: " + tokenType);
        }
        return toTokenClaims(claims);
    }

    @Override
    public void invalidateTempToken(String token) {
        invalidatedTempTokens.add(token);
    }

    @Override
    public void revokeAccessToken(String token) {
        revokedAccessTokens.add(token);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private TokenClaims toTokenClaims(Claims claims) {
        var roleIdRaw = claims.get("role", String.class);
        var permissionsRaw = claims.get("permissions", String.class);
        Set<String> permissions = permissionsRaw != null && !permissionsRaw.isBlank()
                ? Stream.of(permissionsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet())
                : Collections.emptySet();
        return new TokenClaims(
                UUID.fromString(claims.getSubject()),
                roleIdRaw != null ? UUID.fromString(roleIdRaw) : null,
                claims.get("roleName", String.class),
                claims.get("type", String.class),
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant(),
                claims.getIssuer(),
                permissions
        );
    }
}
