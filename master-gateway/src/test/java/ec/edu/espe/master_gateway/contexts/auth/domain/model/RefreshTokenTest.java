package ec.edu.espe.master_gateway.contexts.auth.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

    @Test
    void should_createRefreshToken() {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var expiresAt = LocalDateTime.now().plusDays(1);

        var token = new RefreshToken("rt-123", userId, roleId, expiresAt);

        assertThat(token.getToken()).isEqualTo("rt-123");
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getRoleId()).isEqualTo(roleId);
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(token.isRevoked()).isFalse();
    }

    @Test
    void should_revokeToken() {
        var token = new RefreshToken("rt-123", UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1));

        token.revoke();

        assertThat(token.isRevoked()).isTrue();
        assertThat(token.isValid()).isFalse();
    }

    @Test
    void should_returnIsValid_when_notExpiredAndNotRevoked() {
        var token = new RefreshToken("rt-123", UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1));

        assertThat(token.isValid()).isTrue();
    }

    @Test
    void should_returnIsExpired_when_pastExpiration() {
        var token = new RefreshToken("rt-123", UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().minusDays(1));

        assertThat(token.isExpired()).isTrue();
        assertThat(token.isValid()).isFalse();
    }
}
