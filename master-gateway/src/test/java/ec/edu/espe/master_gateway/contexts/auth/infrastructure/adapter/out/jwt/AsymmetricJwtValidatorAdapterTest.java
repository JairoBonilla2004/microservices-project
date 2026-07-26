package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsymmetricJwtValidatorAdapterTest {

    private static KeyPair keyPair;
    private static String publicKeyPem;

    @Mock
    private RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    private AsymmetricJwtValidatorAdapter adapter;

    @BeforeAll
    static void generateKeyPair() throws NoSuchAlgorithmException {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();

        var publicKey = keyPair.getPublic();
        var encoded = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
        var sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        int index = 0;
        while (index < encoded.length()) {
            int lineEnd = Math.min(index + 64, encoded.length());
            sb.append(encoded, index, lineEnd).append('\n');
            index = lineEnd;
        }
        sb.append("-----END PUBLIC KEY-----");
        publicKeyPem = sb.toString();
    }

    @BeforeEach
    void setUp() {
        adapter = new AsymmetricJwtValidatorAdapter(publicKeyPem, revokedTokenRepositoryPort);
    }

    @Test
    void should_validateValidAccessToken() {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var token = createToken(userId, roleId, "ACCESS_TOKEN");

        var claims = adapter.validate(token);

        assertThat(claims.getUserId()).isEqualTo(userId);
        assertThat(claims.getRoleId()).isEqualTo(roleId);
        assertThat(claims.getTokenType()).isEqualTo("ACCESS_TOKEN");
    }

    @Test
    void should_throw_when_tokenTypeIsNotAccessToken() {
        var token = createToken(UUID.randomUUID(), UUID.randomUUID(), "REFRESH_TOKEN");

        assertThatThrownBy(() -> adapter.validate(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Tipo de token invalido");
    }

    @Test
    void should_throw_when_tokenIsRevoked() {
        var token = createToken(UUID.randomUUID(), UUID.randomUUID(), "ACCESS_TOKEN");
        adapter.revokeAccessToken(token);

        assertThatThrownBy(() -> adapter.validate(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("revocado");
    }

    @Test
    void should_validateTempToken() {
        var userId = UUID.randomUUID();
        var token = createToken(userId, null, "TEMP_TOKEN");

        var claims = adapter.validateTempToken(token);

        assertThat(claims.getUserId()).isEqualTo(userId);
        assertThat(claims.getTokenType()).isEqualTo("TEMP_TOKEN");
    }

    @Test
    void should_throw_when_tempTokenAlreadyUsed() {
        var token = createToken(UUID.randomUUID(), null, "TEMP_TOKEN");
        adapter.invalidateTempToken(token);

        assertThatThrownBy(() -> adapter.validateTempToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("ya fue utilizado");
    }

    @Test
    void should_throw_when_tempTokenTypeIsWrong() {
        var token = createToken(UUID.randomUUID(), null, "ACCESS_TOKEN");

        assertThatThrownBy(() -> adapter.validateTempToken(token))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Tipo de token invalido");
    }

    @Test
    void should_invalidateTempToken() {
        var token = createToken(UUID.randomUUID(), null, "TEMP_TOKEN");

        adapter.invalidateTempToken(token);

        assertThatThrownBy(() -> adapter.validateTempToken(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void should_revokeAccessToken() {
        var token = createToken(UUID.randomUUID(), UUID.randomUUID(), "ACCESS_TOKEN");

        adapter.revokeAccessToken(token);

        assertThatThrownBy(() -> adapter.validate(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void should_throw_whenInvalidPemKeyProvided() {
        assertThatThrownBy(() -> new AsymmetricJwtValidatorAdapter("invalid-pem", revokedTokenRepositoryPort))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Clave publica invalida");
    }

    private String createToken(UUID userId, UUID roleId, String type) {
        var now = Instant.now();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", type)
                .claim("roleName", "ADMIN")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(15))))
                .issuer("master-gateway");
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        return builder.signWith(keyPair.getPrivate()).compact();
    }
}
