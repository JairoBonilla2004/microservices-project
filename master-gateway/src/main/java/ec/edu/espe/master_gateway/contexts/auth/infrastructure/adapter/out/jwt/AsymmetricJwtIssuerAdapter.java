package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jwt.mode", havingValue = "asymmetric")
public class AsymmetricJwtIssuerAdapter implements TokenIssuerPort {

    private static final String ISSUER = "master-gateway";

    private final JwtProperties jwtProperties;
    private final KeyPair keyPair;

    public AsymmetricJwtIssuerAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.keyPair = loadOrGenerateKeyPair(jwtProperties.getPrivateKeyPem());
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
                .issuer(ISSUER);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        if (permissions != null && !permissions.isEmpty()) {
            builder.claim("permissions", String.join(",", permissions));
        }
        return builder.signWith(keyPair.getPrivate()).compact();
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
                .issuer(ISSUER);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        return builder.signWith(keyPair.getPrivate()).compact();
    }

    private String buildToken(UUID userId, UUID roleId, String type, java.time.Duration expiration) {
        var now = Instant.now();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .issuer(ISSUER);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        return builder.signWith(keyPair.getPrivate()).compact();
    }

    public String getPublicKeyPem() {
        try {
            var keySpec = KeyFactory.getInstance("RSA")
                    .getKeySpec(keyPair.getPublic(), X509EncodedKeySpec.class);
            var encoded = Base64.getEncoder().encodeToString(keySpec.getEncoded());
            var sb = new StringBuilder();
            sb.append("-----BEGIN PUBLIC KEY-----\n");
            int index = 0;
            while (index < encoded.length()) {
                int lineEnd = Math.min(index + 64, encoded.length());
                sb.append(encoded, index, lineEnd).append('\n');
                index = lineEnd;
            }
            sb.append("-----END PUBLIC KEY-----");
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo exportar la clave publica RSA", e);
        }
    }

    private static KeyPair loadOrGenerateKeyPair(String privateKeyPem) {
        if (privateKeyPem != null && !privateKeyPem.isBlank()) {
            return loadKeyPairFromPem(privateKeyPem);
        }
        return generateKeyPair();
    }

    private static KeyPair loadKeyPairFromPem(String pem) {
        try {
            var keyBytes = decodePem(pem, "PRIVATE KEY");
            var keySpec = new PKCS8EncodedKeySpec(keyBytes);
            var keyFactory = KeyFactory.getInstance("RSA");
            var privateKey = keyFactory.generatePrivate(keySpec);
            var rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
            var publicKeySpec = new RSAPublicKeySpec(
                    rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
            var publicKey = keyFactory.generatePublic(publicKeySpec);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo cargar el par de llaves RSA desde el PEM configurado", e);
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

    private static KeyPair generateKeyPair() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo generar el par de llaves RSA", e);
        }
    }
}
