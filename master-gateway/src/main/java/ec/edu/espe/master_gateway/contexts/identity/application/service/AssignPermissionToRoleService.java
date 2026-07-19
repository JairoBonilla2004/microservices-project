package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AssignPermissionToRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignPermissionToRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de asignación de permisos a un rol.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_UPDATE}, que el rol exista y que el permiso no esté
 * ya asignado, y luego persiste la asignación del permiso al rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class AssignPermissionToRoleService implements AssignPermissionToRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignPermissionToRoleService.class);

    private final RolePermissionAssignmentRepositoryPort assignmentRepository;
    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public AssignPermissionToRoleService(RolePermissionAssignmentRepositoryPort assignmentRepository,
                                         RoleRepositoryPort roleRepository,
                                         AuthorizationPort authorizationPort) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID roleId, AssignPermissionToRoleRequest request) {
        authorizationPort.requirePermission(Permission.ROLES_UPDATE);

        if (!authorizationPort.hasPermission(request.permission())) {
            throw new AuthorizationException(
                "No puedes asignar el permiso " + request.permission().name()
                    + " porque no lo posees");
        }

        roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Rol", roleId));

        var existing = assignmentRepository.findByRoleIdAndPermission(roleId, request.permission());
        if (existing.isPresent()) {
            throw new DuplicateException("RolePermissionAssignment",
                "roleId and permission",
                roleId + " - " + request.permission());
        }

        RolePermissionAssignment assignment = new RolePermissionAssignment(
            roleId, request.permission(), "system");
        assignmentRepository.save(assignment);
        log.info("Permission {} assigned to role {}", request.permission(), roleId);
    }
}
