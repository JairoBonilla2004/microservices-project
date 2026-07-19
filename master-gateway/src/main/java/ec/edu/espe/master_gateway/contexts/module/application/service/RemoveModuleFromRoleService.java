package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.RemoveModuleFromRoleUseCase;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de eliminación de asignación de módulo a un rol.
 *
 * <p>Valida los permisos del usuario, busca la asignación existente, la revoca
 * y persiste el cambio en el repositorio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class RemoveModuleFromRoleService implements RemoveModuleFromRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(RemoveModuleFromRoleService.class);

    private final RoleModuleAssignmentRepositoryPort assignmentRepository;
    private final AuthorizationPort authorizationPort;

    public RemoveModuleFromRoleService(RoleModuleAssignmentRepositoryPort assignmentRepository,
                                       AuthorizationPort authorizationPort) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID roleId, UUID moduleId) {
        Objects.requireNonNull(roleId);
        Objects.requireNonNull(moduleId);
        authorizationPort.requirePermission(Permission.MODULES_ASSIGN);

        var assignment = assignmentRepository.findByRoleIdAndModuleId(roleId, moduleId)
                .orElseThrow(() -> new NotFoundException("RoleModuleAssignment", roleId + " - " + moduleId));
        assignment.revoke();
        assignmentRepository.save(assignment);

        log.info("Module removed from role: roleId={}, moduleId={}", roleId, moduleId);
    }
}
