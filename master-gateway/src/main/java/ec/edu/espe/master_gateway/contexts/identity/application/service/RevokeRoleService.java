package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.RevokeRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de revocación de asignación de un rol a un usuario.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_ASSIGN_USERS}, busca la asignación correspondiente
 * y la revoca, eliminando el rol del usuario.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class RevokeRoleService implements RevokeRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(RevokeRoleService.class);

    private final UserRoleAssignmentRepositoryPort assignmentRepository;
    private final AuthorizationPort authorizationPort;

    public RevokeRoleService(UserRoleAssignmentRepositoryPort assignmentRepository,
                             AuthorizationPort authorizationPort) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID userId, UUID roleId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(roleId);
        authorizationPort.requirePermission(Permission.ROLES_ASSIGN_USERS);

        UUID currentUserId = authorizationPort.getCurrentUserId();
        if (currentUserId.equals(userId)) {
            throw new IllegalArgumentException("No puedes revocarte tu propio rol");
        }

        assignmentRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new NotFoundException("UserRoleAssignment", userId + " - " + roleId));

        // Eliminación física en la tabla pivote (no soft delete): la relación
        // usuario-rol se rompe por completo, sin conservar histórico revocado.
        assignmentRepository.hardDeleteByUserIdAndRoleId(userId, roleId);
        log.info("Role {} revoked (hard delete) from user {}", roleId, userId);
    }
}
