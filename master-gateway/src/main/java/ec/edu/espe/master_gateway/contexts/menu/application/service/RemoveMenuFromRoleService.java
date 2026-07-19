package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.menu.application.port.in.RemoveMenuFromRoleUseCase;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
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
 * Servicio que implementa el caso de uso de eliminación de asignación de menú a un rol.
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
public class RemoveMenuFromRoleService implements RemoveMenuFromRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(RemoveMenuFromRoleService.class);

    private final RoleMenuAssignmentRepositoryPort assignmentRepository;
    private final AuthorizationPort authorizationPort;

    public RemoveMenuFromRoleService(RoleMenuAssignmentRepositoryPort assignmentRepository,
                                     AuthorizationPort authorizationPort) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID roleId, UUID menuNodeId) {
        Objects.requireNonNull(roleId);
        Objects.requireNonNull(menuNodeId);
        authorizationPort.requirePermission(Permission.MENUS_ASSIGN);

        var assignment = assignmentRepository.findByRoleIdAndMenuNodeId(roleId, menuNodeId)
                .orElseThrow(() -> new NotFoundException("RoleMenuAssignment", roleId + " - " + menuNodeId));
        assignment.revoke();
        assignmentRepository.save(assignment);

        log.info("Menu removed from role: roleId={}, menuNodeId={}", roleId, menuNodeId);
    }
}
