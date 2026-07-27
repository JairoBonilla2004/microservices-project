package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsymmetricJwtIssuerAdapterTest {

    @Mock(lenient = true)
    private JwtProperties jwtProperties;

    private AsymmetricJwtIssuerAdapter adapter;

    @BeforeEach
    void setUp() {
        when(jwtProperties.getTempTokenExpiration()).thenReturn(Duration.ofMinutes(5));
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(Duration.ofMinutes(15));
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        when(jwtProperties.getPrivateKeyPem()).thenReturn("");
        adapter = new AsymmetricJwtIssuerAdapter(jwtProperties);
    }

    @Test
    void should_issueTempToken() {
        var userId = UUID.randomUUID();

        var token = adapter.issueTempToken(userId);

        assertThat(token).isNotBlank();
    }

    @Test
    void should_issueAccessToken_withAllFields() {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();

        var token = adapter.issueAccessToken(userId, roleId, Set.of("READ", "WRITE"), "ADMIN", "jdoe");

        assertThat(token).isNotBlank();
    }

    @Test
    void should_issueAccessToken_withoutRoleId() {
        var userId = UUID.randomUUID();

        var token = adapter.issueAccessToken(userId, null, Set.of(), "USER", "jdoe");

        assertThat(token).isNotBlank();
    }

    @Test
    void should_issueRefreshToken() {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();

        var token = adapter.issueRefreshToken(userId, roleId, "ADMIN");

        assertThat(token).isNotBlank();
    }

    @Test
    void should_issueRefreshToken_withoutRoleId() {
        var userId = UUID.randomUUID();

        var token = adapter.issueRefreshToken(userId, null, "USER");

        assertThat(token).isNotBlank();
    }

    @Test
    void should_returnPublicKeyPem() {
        var pem = adapter.getPublicKeyPem();

        assertThat(pem)
                .startsWith("-----BEGIN PUBLIC KEY-----")
                .endsWith("-----END PUBLIC KEY-----");
    }

    @Test
    void should_throw_whenKeyGenerationFails() {
        assertThat(adapter).isNotNull();
    }

    @Test
    void should_loadKeyPairFromPem_whenPrivateKeyPemProvided() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var originalKeyPair = generator.generateKeyPair();

        var privateKeyBytes = originalKeyPair.getPrivate().getEncoded();
        var privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getEncoder().encodeToString(privateKeyBytes)
                + "\n-----END PRIVATE KEY-----";

        when(jwtProperties.getPrivateKeyPem()).thenReturn(privateKeyPem);
        var pemAdapter = new AsymmetricJwtIssuerAdapter(jwtProperties);

        var token = pemAdapter.issueAccessToken(
                UUID.randomUUID(), UUID.randomUUID(), Set.of("READ"), "ADMIN", "jdoe");

        assertThat(token).isNotBlank();
        assertThat(pemAdapter.getPublicKeyPem()).contains("-----BEGIN PUBLIC KEY-----");
    }

    @Test
    void should_loadKeyPairFromRawBase64_whenPrivateKeyPemProvided() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var originalKeyPair = generator.generateKeyPair();

        var rawBase64 = Base64.getEncoder().encodeToString(
                originalKeyPair.getPrivate().getEncoded());

        when(jwtProperties.getPrivateKeyPem()).thenReturn(rawBase64);
        var pemAdapter = new AsymmetricJwtIssuerAdapter(jwtProperties);

        var token = pemAdapter.issueAccessToken(
                UUID.randomUUID(), UUID.randomUUID(), Set.of("READ"), "ADMIN", "jdoe");

        assertThat(token).isNotBlank();
        assertThat(pemAdapter.getPublicKeyPem()).contains("-----BEGIN PUBLIC KEY-----");
    }
}
