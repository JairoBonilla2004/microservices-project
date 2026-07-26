package ec.edu.espe.master_gateway.contexts.auth.application.service;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.LoginUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginResponse;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RoleInfo;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenIssuerPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación de usuarios.
 *
 * <p>Implementa el flujo completo de inicio de sesión incluyendo
 * validación de credenciales, verificación de roles activos y emisión
 * de token temporal. Sigue la regla de seguridad OWASP de no revelar
 * si el error está en el usuario o en la contraseña.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class LoginService implements LoginUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final TokenIssuerPort tokenIssuerPort;

    /**
     * Inicializa el servicio con los puertos de infraestructura necesarios.
     *
     * @param userRepositoryPort                 repositorio de usuarios
     * @param passwordHasherPort                 servicio de hashing de contraseñas
     * @param userRoleAssignmentRepositoryPort   repositorio de asignaciones usuario-rol
     * @param roleRepositoryPort                 repositorio de roles
     * @param tokenIssuerPort                    servicio de emisión de tokens
     */
    public LoginService(UserRepositoryPort userRepositoryPort,
                        PasswordHasherPort passwordHasherPort,
                        UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort,
                        RoleRepositoryPort roleRepositoryPort,
                        TokenIssuerPort tokenIssuerPort) {
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort);
        this.passwordHasherPort = Objects.requireNonNull(passwordHasherPort);
        this.userRoleAssignmentRepositoryPort = Objects.requireNonNull(userRoleAssignmentRepositoryPort);
        this.roleRepositoryPort = Objects.requireNonNull(roleRepositoryPort);
        this.tokenIssuerPort = Objects.requireNonNull(tokenIssuerPort);
    }

    /**
     * Ejecuta el inicio de sesión validando credenciales y generando
     * un token temporal con los roles activos del usuario.
     *
     * @param request credenciales del usuario
     * @return respuesta con token temporal y lista de roles disponibles
     * @throws AuthenticationException si las credenciales son inválidas
     *                                 o el usuario no tiene roles activos
     */
    @Override
    public LoginResponse execute(LoginRequest request) {
        var username = request.username();
        var user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Failed login attempt for username: {}", username);
                    return new AuthenticationException("Credenciales inválidas");
                });

        if (!user.isActive()) {
            log.warn("Failed login attempt for inactive user: {}", username);
            throw new AuthenticationException("Credenciales inválidas");
        }

        if (!passwordHasherPort.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt for username: {} (invalid password)", username);
            throw new AuthenticationException("Credenciales inválidas");
        }

        var assignments = userRoleAssignmentRepositoryPort.findByUserId(user.getId()); //asignments = asignaciones de roles del usuario

        var activeAssignments = assignments.stream()
                .filter(a -> a.isActive())
                .toList();

        var tempToken = tokenIssuerPort.issueTempToken(user.getId());

        var roles = activeAssignments.stream()
                .map(a -> {
                    var role = roleRepositoryPort.findById(a.getRoleId())
                            .orElseThrow(() -> {
                                log.warn("Role {} not found for user {}, possible inconsistent data", a.getRoleId(), username);
                                return new NotFoundException("Rol", a.getRoleId());
                            });
                    return new RoleInfo(a.getRoleId(), role.getNombre());
                })
                .toList();

        log.info("Successful login for user: {}", username);
        return new LoginResponse(tempToken, roles);
    }
}
