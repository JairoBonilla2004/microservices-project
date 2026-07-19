package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.DeactivateRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de desactivación de roles.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_DELETE}, busca el rol por su identificador y lo
 * desactiva, impidiendo su uso futuro en el sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class DeactivateRoleService implements DeactivateRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeactivateRoleService.class);

    private final RoleRepositoryPort roleRepository;
    private final UserRoleAssignmentRepositoryPort assignmentRepository;
    private final AuthorizationPort authorizationPort;

    public DeactivateRoleService(RoleRepositoryPort roleRepository,
                                 UserRoleAssignmentRepositoryPort assignmentRepository,
                                 AuthorizationPort authorizationPort) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID id) {
        Objects.requireNonNull(id);
        authorizationPort.requirePermission(Permission.ROLES_DELETE);

        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Rol", id));

        long activeUsers = assignmentRepository.findByRoleId(id).size();
        if (activeUsers > 0) {
            throw new IllegalStateException(
                "No puedes desactivar el rol \"" + role.getNombre() + "\" porque tiene "
                + activeUsers + " usuario(s) activo(s) asignados");
        }

        role.deactivate();
        roleRepository.save(role);
        log.info("Role deactivated with id: {}", id);
    }
}
