package ec.edu.espe.master_gateway.contexts.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.TokenValidationResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.TokenIssuerFactory;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateTokenServiceTest {

    @Mock
    private TokenIssuerFactory tokenIssuerFactory;
    @Mock
    private ServiceRegistryRepositoryPort serviceRegistryRepositoryPort;
    @Mock
    private TokenValidationPort defaultValidator;

    @InjectMocks
    private ValidateTokenService validateTokenService;

    private String validToken;
    private UUID userId;
    private UUID roleId;
    private TokenClaims validClaims;

    @BeforeEach
    void setUp() {
        validToken = "valid-jwt-token";
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        validClaims = new TokenClaims(userId, roleId, "ADMIN", "access",
                Instant.now(), Instant.now().plusSeconds(3600), "issuer", Set.of());
    }

    @Test
    void should_returnValidResponse_when_tokenIsValid() {
        when(tokenIssuerFactory.getDefaultValidator()).thenReturn(defaultValidator);
        when(defaultValidator.validate(validToken)).thenReturn(validClaims);

        TokenValidationResponse response = validateTokenService.execute(validToken, null);

        assertThat(response.valid()).isTrue();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.roleId()).isEqualTo(roleId);
    }

    @Test
    void should_returnInvalidResponse_when_tokenIsRevoked() {
        when(tokenIssuerFactory.getDefaultValidator()).thenReturn(defaultValidator);
        when(defaultValidator.validate(validToken)).thenThrow(new RuntimeException("Token has been revoked"));

        TokenValidationResponse response = validateTokenService.execute(validToken, null);

        assertThat(response.valid()).isFalse();
        assertThat(response.message()).contains("revoked");
    }

    @Test
    void should_returnInvalidResponse_when_tokenIsMalformed() {
        String badToken = "malformed-token";
        when(tokenIssuerFactory.getDefaultValidator()).thenReturn(defaultValidator);
        when(defaultValidator.validate(badToken)).thenThrow(new RuntimeException("Invalid token"));

        TokenValidationResponse response = validateTokenService.execute(badToken, null);

        assertThat(response.valid()).isFalse();
        assertThat(response.message()).contains("Invalid");
    }
}
