package ec.edu.espe.master_gateway.shared.infrastructure.security;

/**
 * Adaptador de autorización basado en Spring Security.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort}
 * utilizando el contexto de seguridad de Spring Security para obtener el usuario
 * autenticado y verificar sus permisos. Aplica el principio de verificación de
 * permisos sobre las autoridades cargadas en el contexto de seguridad.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthorizationAdapter implements AuthorizationPort {

    static final String PERMISSION_PREFIX = "PERMISSION_";

    @Override
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthorizationException("Usuario no autenticado");
        }
        Object principal = auth.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)) {
            throw new AuthorizationException("Usuario no autenticado");
        }
        return UUID.fromString(principal.toString());
    }

    @Override
    public boolean hasPermission(Permission permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(PERMISSION_PREFIX + permission.name()));
    }

    @Override
    public void requirePermission(Permission requiredPermission) {
        if (!hasPermission(requiredPermission)) {
            throw new AuthorizationException(
                "Permiso requerido: " + requiredPermission.name()
            );
        }
    }

    @Override
    public void requireOwnershipOrPermission(UUID resourceOwnerId, Permission requiredPermission) {
        UUID currentUserId = getCurrentUserId();
        if (currentUserId.equals(resourceOwnerId)) {
            return;
        }
        requirePermission(requiredPermission);
    }
}
