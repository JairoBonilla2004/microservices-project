package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.AssignMenuToRoleUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.AssignMenuToRoleRequest;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de asignación de un nodo de menú a un rol.
 *
 * <p>Verifica que la asignación no exista previamente para evitar duplicados,
 * valida los permisos del usuario y persiste la nueva asignación en el repositorio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class AssignMenuToRoleService implements AssignMenuToRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignMenuToRoleService.class);

    private final RoleMenuAssignmentRepositoryPort roleMenuAssignmentRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public AssignMenuToRoleService(RoleMenuAssignmentRepositoryPort roleMenuAssignmentRepositoryPort,
                                   AuthorizationPort authorizationPort) {
        this.roleMenuAssignmentRepositoryPort = Objects.requireNonNull(roleMenuAssignmentRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(AssignMenuToRoleRequest request) {
        authorizationPort.requirePermission(Permission.MENUS_ASSIGN);

        var existing = roleMenuAssignmentRepositoryPort.findByRoleIdAndMenuNodeId(
                request.roleId(), request.menuNodeId());
        if (existing.isPresent()) {
            throw new DuplicateException("RoleMenuAssignment",
                "roleId and menuNodeId",
                request.roleId() + " - " + request.menuNodeId());
        }

        RoleMenuAssignment assignment = new RoleMenuAssignment(
            request.roleId(), request.menuNodeId(), "system");
        roleMenuAssignmentRepositoryPort.save(assignment);

        log.info("Menu assigned to role: roleId={}, menuNodeId={}", request.roleId(), request.menuNodeId());
    }
}
