package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.RemovePermissionFromRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de eliminación de permisos de un rol.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_UPDATE}, busca la asignación del permiso al rol y
 * la revoca, eliminando efectivamente el permiso del rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class RemovePermissionFromRoleService implements RemovePermissionFromRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(RemovePermissionFromRoleService.class);

    private final RolePermissionAssignmentRepositoryPort assignmentRepository;
    private final AuthorizationPort authorizationPort;

    public RemovePermissionFromRoleService(RolePermissionAssignmentRepositoryPort assignmentRepository,
                                           AuthorizationPort authorizationPort) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID roleId, Permission permission) {
        Objects.requireNonNull(roleId);
        Objects.requireNonNull(permission);
        authorizationPort.requirePermission(Permission.ROLES_UPDATE);

        if (!authorizationPort.hasPermission(permission)) {
            throw new AuthorizationException(
                "No puedes remover el permiso " + permission.name()
                    + " porque no lo posees");
        }

        var assignment = assignmentRepository.findByRoleIdAndPermission(roleId, permission)
                .orElseThrow(() -> new NotFoundException("RolePermissionAssignment", roleId + " - " + permission));
        assignment.revoke();
        assignmentRepository.save(assignment);
        log.info("Permission {} removed from role {}", permission, roleId);
    }
}
