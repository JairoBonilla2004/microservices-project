package ec.edu.espe.master_gateway.shared.infrastructure.security;

/**
 * Resolvedor de permisos que consulta la base de datos.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.shared.domain.port.out.PermissionResolverPort}
 * obteniendo los permisos asignados a un rol a través de los repositorios de dominio.
 * Utiliza los puertos de salida de identidad para realizar las consultas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.PermissionResolverPort;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabasePermissionResolver implements PermissionResolverPort {

    private final RolePermissionAssignmentRepositoryPort assignmentRepository;
    private final RoleRepositoryPort roleRepository;

    public DatabasePermissionResolver(RolePermissionAssignmentRepositoryPort assignmentRepository,
                                      RoleRepositoryPort roleRepository) {
        this.assignmentRepository = assignmentRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<Permission> resolvePermissions(String roleName) {
        if (roleName == null) {
            return EnumSet.noneOf(Permission.class);
        }

        var role = roleRepository.findAllActive().stream()
                .filter(r -> r.getNombre().equalsIgnoreCase(roleName))
                .findFirst();

        if (role.isEmpty()) {
            return EnumSet.noneOf(Permission.class);
        }

        UUID roleId = role.get().getId();
        var dbPermissions = assignmentRepository.findPermissionsByRoleId(roleId);

        if (!dbPermissions.isEmpty()) {
            return EnumSet.copyOf(dbPermissions);
        }

        return EnumSet.noneOf(Permission.class);
    }
}
