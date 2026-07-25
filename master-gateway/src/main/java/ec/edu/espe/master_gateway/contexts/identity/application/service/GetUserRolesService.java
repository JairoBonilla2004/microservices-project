package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetUserRolesUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetUserRolesService implements GetUserRolesUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetUserRolesService.class);

    private final UserRepositoryPort userRepository;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepository;
    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public GetUserRolesService(UserRepositoryPort userRepository,
                                UserRoleAssignmentRepositoryPort userRoleAssignmentRepository,
                                RoleRepositoryPort roleRepository,
                                AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.userRoleAssignmentRepository = Objects.requireNonNull(userRoleAssignmentRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Obtiene todos los roles asignados a un usuario.
     *
     * <p>Verifica la existencia del usuario, consulta las asignaciones
     * y recupera la información de cada rol asignado.</p>
     *
     * @param userId identificador único del usuario
     * @return lista de respuestas con la información de los roles asignados
     * @throws NotFoundException si no existe un usuario con el ID proporcionado
     */
    @Override
    public List<RoleResponse> execute(UUID userId) {
        Objects.requireNonNull(userId);
        authorizationPort.requireOwnershipOrPermission(userId, Permission.USERS_READ);

        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Usuario", userId));

        log.debug("Retrieving roles for user: {}", userId);
        return userRoleAssignmentRepository.findByUserId(userId).stream()
            .map(UserRoleAssignment::getRoleId)
            .map(roleId -> roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Rol", roleId)))
            .map(role -> new RoleResponse(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.getEstado().name(),
                role.getFechaCreacion()
            ))
            .toList();
    }
}
