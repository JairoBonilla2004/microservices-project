package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AssignRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignRoleRequest;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de asignación de un rol a un usuario.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_ASSIGN_USERS}, que tanto el usuario como el rol
 * existan, que la asignación no esté duplicada, y luego persiste
 * la asignación del rol al usuario.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class AssignRoleService implements AssignRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignRoleService.class);

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepository;
    private final AuthorizationPort authorizationPort;

    public AssignRoleService(UserRepositoryPort userRepository,
                             RoleRepositoryPort roleRepository,
                             UserRoleAssignmentRepositoryPort userRoleAssignmentRepository,
                             AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.userRoleAssignmentRepository = Objects.requireNonNull(userRoleAssignmentRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(AssignRoleRequest request) {
        Objects.requireNonNull(request);
        authorizationPort.requirePermission(Permission.ROLES_ASSIGN_USERS);

        UUID currentUserId = authorizationPort.getCurrentUserId();
        if (currentUserId.equals(request.userId())) {
            throw new IllegalArgumentException("No puedes asignarte un rol a ti mismo");
        }

        userRepository.findById(request.userId())
            .orElseThrow(() -> new NotFoundException("Usuario", request.userId()));

        roleRepository.findById(request.roleId())
            .orElseThrow(() -> new NotFoundException("Rol", request.roleId()));

        if (userRoleAssignmentRepository.findByUserIdAndRoleId(request.userId(), request.roleId()).isPresent()) {
            throw new DuplicateException("Asignación", "userId y roleId",
                request.userId() + ", " + request.roleId());
        }

        UserRoleAssignment assignment = new UserRoleAssignment(request.userId(), request.roleId(), "system");
        userRoleAssignmentRepository.save(assignment);
        log.info("Role {} assigned to user {}", request.roleId(), request.userId());
    }
}
