package ec.edu.espe.master_gateway.contexts.auth.application.service;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.RefreshTokenUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import ec.edu.espe.master_gateway.shared.domain.port.out.PermissionResolverPort;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de renovación de tokens de acceso.
 *
 * <p>Implementa el flujo de renovación de tokens JWT utilizando un
 * token de actualización válido. Invalida el token anterior, verifica
 * la vigencia del rol y emite un nuevo par de tokens de acceso y
 * actualización con los permisos resueltos del rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenIssuerPort tokenIssuerPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionResolverPort permissionResolverPort;
    private final UserRepositoryPort userRepositoryPort;
    private final Duration refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                                TokenIssuerPort tokenIssuerPort,
                                RoleRepositoryPort roleRepositoryPort,
                                PermissionResolverPort permissionResolverPort,
                                UserRepositoryPort userRepositoryPort,
                                JwtProperties jwtProperties) {
        this.refreshTokenRepositoryPort = Objects.requireNonNull(refreshTokenRepositoryPort);
        this.tokenIssuerPort = Objects.requireNonNull(tokenIssuerPort);
        this.roleRepositoryPort = Objects.requireNonNull(roleRepositoryPort);
        this.permissionResolverPort = Objects.requireNonNull(permissionResolverPort);
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort);
        this.refreshTokenExpiration = Objects.requireNonNull(jwtProperties.getRefreshTokenExpiration());
    }

    @Override
    public RefreshTokenResponse execute(RefreshTokenRequest request) {
        var existing = refreshTokenRepositoryPort.findByToken(request.refreshToken())
                .orElseThrow(() -> {
                    log.warn("Refresh token attempt with invalid token");
                    return new AuthenticationException("Token de refresco inválido");
                });

        if (!existing.isValid()) {
            log.warn("Refresh token attempt with expired or revoked token for userId: {}", existing.getUserId());
            throw new AuthenticationException("Token de refresco expirado o revocado");
        }

        existing.revoke();
        refreshTokenRepositoryPort.save(existing);

        var role = roleRepositoryPort.findById(existing.getRoleId())
                .orElseThrow(() -> {
                    log.warn("Refresh token attempt with missing role for userId: {}", existing.getUserId());
                    return new AuthenticationException("Rol no encontrado");
                });

        var permissions = permissionResolverPort.resolvePermissions(role.getNombre());

        var permissionStrings = permissions.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        var username = userRepositoryPort.findById(existing.getUserId())
                .map(User::getUsername)
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

        var accessToken = tokenIssuerPort.issueAccessToken(existing.getUserId(), existing.getRoleId(), permissionStrings, role.getNombre(), username);
        var newRefreshTokenValue = tokenIssuerPort.issueRefreshToken(existing.getUserId(), existing.getRoleId(), role.getNombre());

        var newRefreshToken = new RefreshToken(
                newRefreshTokenValue,
                existing.getUserId(),
                existing.getRoleId(),
                LocalDateTime.now(ZoneOffset.UTC).plus(refreshTokenExpiration)
        );
        refreshTokenRepositoryPort.save(newRefreshToken);

        log.info("Tokens refreshed successfully for userId: {}", existing.getUserId());
        return new RefreshTokenResponse(accessToken, newRefreshTokenValue, 3600L);
    }
}
