package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymmetricJwtIssuerAdapterTest {

    @Mock(lenient = true)
    private JwtProperties jwtProperties;

    private SymmetricJwtIssuerAdapter adapter;

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn("this-is-a-secret-key-that-is-long-enough-for-hmac");
        when(jwtProperties.getTempTokenExpiration()).thenReturn(Duration.ofMinutes(5));
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(Duration.ofMinutes(15));
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        adapter = new SymmetricJwtIssuerAdapter(jwtProperties);
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
        var permissions = Set.of("USER_READ", "USER_WRITE");

        var token = adapter.issueAccessToken(userId, roleId, permissions, "ADMIN", "jdoe");

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
}
