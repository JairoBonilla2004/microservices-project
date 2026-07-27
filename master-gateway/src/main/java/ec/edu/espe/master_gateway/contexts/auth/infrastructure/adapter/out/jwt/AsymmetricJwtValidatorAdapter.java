package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

/**
 * Adaptador de validación de tokens JWT utilizando el algoritmo asimétrico RSA.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort}
 * verificando la firma de los tokens con una clave pública RSA. Esta clase es
 * instanciada por {@link ec.edu.espe.master_gateway.bootstrap.config.JwtConfig} como bean
 * de Spring en modo {@code asymmetric}.</p>
 *
 * <p>La clave pública se recibe como un string en formato PEM (Base64) y se
 * convierte internamente a un objeto {@link java.security.PublicKey} utilizando
 * {@link java.security.spec.X509EncodedKeySpec}. Incluye también un mecanismo
 * en memoria para invalidar tokens temporales.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AsymmetricJwtValidatorAdapter implements TokenValidationPort {

    private final PublicKey publicKey;
    private final Set<String> invalidatedTempTokens = ConcurrentHashMap.newKeySet();
    private final Set<String> revokedAccessTokens = ConcurrentHashMap.newKeySet();
    private final RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    public AsymmetricJwtValidatorAdapter(String publicKeyPem,
                                         RevokedTokenRepositoryPort revokedTokenRepositoryPort) {
        this.publicKey = parsePublicKey(publicKeyPem);
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
                .verifyWith(publicKey)
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

    private static PublicKey parsePublicKey(String pem) {
        try {
            var keyBytes = decodePem(pem, "PUBLIC KEY");
            var spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Clave publica invalida", e);
        }
    }

    private static byte[] decodePem(String pem, String label) {
        if (pem == null || pem.isBlank()) {
            throw new IllegalArgumentException("PEM no puede estar vacio");
        }
        if (!pem.startsWith("-----")) {
            pem = "-----BEGIN " + label + "-----\n" + pem + "\n-----END " + label + "-----";
        }
        return Base64.getDecoder().decode(
                pem.replace("-----BEGIN " + label + "-----", "")
                        .replace("-----END " + label + "-----", "")
                        .replaceAll("\\s", "")
        );
    }
}
