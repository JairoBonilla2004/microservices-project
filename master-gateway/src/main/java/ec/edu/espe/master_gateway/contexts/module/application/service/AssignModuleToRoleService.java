package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.AssignModuleToRoleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.AssignModuleToRoleRequest;
import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de asignación de un módulo a un rol.
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
public class AssignModuleToRoleService implements AssignModuleToRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignModuleToRoleService.class);

    private final RoleModuleAssignmentRepositoryPort roleModuleAssignmentRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public AssignModuleToRoleService(RoleModuleAssignmentRepositoryPort roleModuleAssignmentRepositoryPort,
                                     AuthorizationPort authorizationPort) {
        this.roleModuleAssignmentRepositoryPort = Objects.requireNonNull(roleModuleAssignmentRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(AssignModuleToRoleRequest request) {
        authorizationPort.requirePermission(Permission.MODULES_ASSIGN);

        var existing = roleModuleAssignmentRepositoryPort.findByRoleIdAndModuleId(
                request.roleId(), request.moduleId());
        if (existing.isPresent()) {
            throw new DuplicateException("RoleModuleAssignment",
                "roleId and moduleId",
                request.roleId() + " - " + request.moduleId());
        }

        RoleModuleAssignment assignment = new RoleModuleAssignment(
            request.roleId(), request.moduleId(), "system");
        roleModuleAssignmentRepositoryPort.save(assignment);

        log.info("Module assigned to role: roleId={}, moduleId={}", request.roleId(), request.moduleId());
    }
}
