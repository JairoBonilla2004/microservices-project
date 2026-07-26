package ec.edu.espe.master_gateway.contexts.auth.application.service;

import ec.edu.espe.master_gateway.bootstrap.config.JwtProperties;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.SelectRoleUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.port.out.PermissionResolverPort;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de selección de rol.
 *
 * <p>Implementa el flujo de selección de rol tras el inicio de sesión.
 * Valida el token temporal, verifica que el usuario tenga el rol
 * solicitado, emite los tokens de acceso y actualización, e invalida
 * el token temporal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class SelectRoleService implements SelectRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(SelectRoleService.class);

    private final TokenValidationPort tokenValidationPort;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;
    private final TokenIssuerPort tokenIssuerPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionResolverPort permissionResolverPort;
    private final UserRepositoryPort userRepositoryPort;
    private final Duration refreshTokenExpiration;

    public SelectRoleService(TokenValidationPort tokenValidationPort,
                             UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort,
                             TokenIssuerPort tokenIssuerPort,
                             RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                             RoleRepositoryPort roleRepositoryPort,
                             PermissionResolverPort permissionResolverPort,
                             UserRepositoryPort userRepositoryPort,
                             JwtProperties jwtProperties) {
        this.tokenValidationPort = Objects.requireNonNull(tokenValidationPort);
        this.userRoleAssignmentRepositoryPort = Objects.requireNonNull(userRoleAssignmentRepositoryPort);
        this.tokenIssuerPort = Objects.requireNonNull(tokenIssuerPort);
        this.refreshTokenRepositoryPort = Objects.requireNonNull(refreshTokenRepositoryPort);
        this.roleRepositoryPort = Objects.requireNonNull(roleRepositoryPort);
        this.permissionResolverPort = Objects.requireNonNull(permissionResolverPort);
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort);
        this.refreshTokenExpiration = Objects.requireNonNull(jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Ejecuta la selección de rol validando el token temporal y la
     * pertenencia del rol al usuario, luego emite los tokens definitivos.
     *
     * @param request token temporal y rol seleccionado
     * @return respuesta con tokens de acceso y actualización
     * @throws AuthorizationException si el usuario no tiene el rol solicitado
     */
    @Override
    public SelectRoleResponse execute(SelectRoleRequest request) {
        TokenClaims claims;
        try {
            claims = tokenValidationPort.validateTempToken(request.tempToken());
            log.info("Temp token validated successfully for userId: {}", claims.getUserId());
        } catch (JwtException e) {
            log.warn("Invalid or expired temp token");
            throw new AuthenticationException("Token temporal inválido o expirado");
        }
        var userId = claims.getUserId();

        userRoleAssignmentRepositoryPort
                .findByUserIdAndRoleId(userId, request.roleId())
                .orElseThrow(() -> new AuthorizationException("El usuario no tiene el rol solicitado"));

        var role = roleRepositoryPort.findById(request.roleId())
                .orElseThrow(() -> new AuthorizationException("Rol no encontrado"));

        var permissions = permissionResolverPort.resolvePermissions(role.getNombre());
        var permissionStrings = permissions.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        var username = userRepositoryPort.findById(userId)
                .map(User::getUsername)
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

        var accessToken = tokenIssuerPort.issueAccessToken(userId, request.roleId(), permissionStrings, role.getNombre(), username);
        var refreshTokenValue = tokenIssuerPort.issueRefreshToken(userId, request.roleId(), role.getNombre());

        var refreshToken = new RefreshToken(
                refreshTokenValue,
                userId,
                request.roleId(),
                LocalDateTime.now().plus(refreshTokenExpiration)
        );
        refreshTokenRepositoryPort.save(refreshToken);

        tokenValidationPort.invalidateTempToken(request.tempToken());

        log.info("Role selected for userId: {} with roleId: {}", userId, request.roleId());
        return new SelectRoleResponse(accessToken, refreshTokenValue, 3600L);
    }
}
