package ec.edu.espe.master_gateway.contexts.auth.application.service;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.LogoutUseCase;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RevokedToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogoutService implements LogoutUseCase {

    private static final Logger log = LoggerFactory.getLogger(LogoutService.class);

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenValidationPort tokenValidationPort;
    private final RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                         TokenValidationPort tokenValidationPort,
                         RevokedTokenRepositoryPort revokedTokenRepositoryPort) {
        this.refreshTokenRepositoryPort = Objects.requireNonNull(refreshTokenRepositoryPort);
        this.tokenValidationPort = Objects.requireNonNull(tokenValidationPort);
        this.revokedTokenRepositoryPort = Objects.requireNonNull(revokedTokenRepositoryPort);
    }

    @Override
    public void execute(String refreshToken, String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            TokenClaims claims;
            try {
                claims = tokenValidationPort.validate(accessToken);
            } catch (Exception e) {
                log.warn("Logout failed: invalid access token");
                throw new AuthenticationException("Token de acceso inválido");
            }
            revokedTokenRepositoryPort.save(new RevokedToken(accessToken, claims.getUserId()));
            tokenValidationPort.revokeAccessToken(accessToken);
            log.info("User {} logged out successfully", claims.getUserId());
        }
        refreshTokenRepositoryPort.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepositoryPort.save(token);
                });
    }
}
