package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymmetricJwtValidatorAdapterTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    private SymmetricJwtValidatorAdapter adapter;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        var encoded = Base64.getEncoder().encodeToString(new byte[32]);
        when(jwtProperties.getSecret()).thenReturn(encoded);
        secretKey = Keys.hmacShaKeyFor(encoded.getBytes());
        adapter = new SymmetricJwtValidatorAdapter(jwtProperties, revokedTokenRepositoryPort);
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
    void should_returnPermissions_when_tokenHasPermissions() {
        var userId = UUID.randomUUID();
        var now = Instant.now();
        var token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", "ACCESS_TOKEN")
                .claim("roleName", "ADMIN")
                .claim("role", UUID.randomUUID().toString())
                .claim("permissions", "READ,WRITE")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(15))))
                .issuer("master-gateway")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        var claims = adapter.validate(token);

        assertThat(claims.getPermissions()).contains("READ", "WRITE");
    }

    @Test
    void should_returnEmptyPermissions_when_tokenHasNoPermissions() {
        var userId = UUID.randomUUID();
        var now = Instant.now();
        var token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", "ACCESS_TOKEN")
                .claim("roleName", "ADMIN")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(15))))
                .issuer("master-gateway")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        var claims = adapter.validate(token);

        assertThat(claims.getPermissions()).isEmpty();
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
                .issuer("master-gateway")
                .signWith(secretKey, Jwts.SIG.HS256);
        if (roleId != null) {
            builder.claim("role", roleId.toString());
        }
        return builder.compact();
    }
}
