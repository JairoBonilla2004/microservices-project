package ec.edu.espe.master_gateway.contexts.auth.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RevokedToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock
    private TokenValidationPort tokenValidationPort;
    @Mock
    private RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    @InjectMocks
    private LogoutService logoutService;

    private String accessToken;
    private String refreshToken;
    private UUID userId;
    private TokenClaims claims;

    @BeforeEach
    void setUp() {
        accessToken = "access-token-123";
        refreshToken = "refresh-token-456";
        userId = UUID.randomUUID();
        claims = new TokenClaims(userId, UUID.randomUUID(), "ADMIN", "access",
                Instant.now(), Instant.now().plusSeconds(3600), "issuer", Set.of());
    }

    @Test
    void should_logoutSuccessfully_when_tokensAreValid() {
        when(tokenValidationPort.validate(accessToken)).thenReturn(claims);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, userId, UUID.randomUUID(),
                java.time.LocalDateTime.now().plusDays(1));
        when(refreshTokenRepositoryPort.findByToken(refreshToken))
                .thenReturn(Optional.of(refreshTokenEntity));

        logoutService.execute(refreshToken, accessToken);

        verify(revokedTokenRepositoryPort).save(any(RevokedToken.class));
        verify(tokenValidationPort).revokeAccessToken(accessToken);
        verify(refreshTokenRepositoryPort).save(refreshTokenEntity);
    }

    @Test
    void should_throwAuthenticationException_when_accessTokenIsInvalid() {
        when(tokenValidationPort.validate(accessToken)).thenThrow(new RuntimeException("invalid"));

        assertThrows(AuthenticationException.class,
                () -> logoutService.execute(refreshToken, accessToken));

        verify(revokedTokenRepositoryPort, never()).save(any());
    }
}
