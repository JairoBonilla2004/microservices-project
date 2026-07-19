package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetRoleUsersUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
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
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetRoleUsersService implements GetRoleUsersUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetRoleUsersService.class);

    private final RoleRepositoryPort roleRepository;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepository;
    private final UserRepositoryPort userRepository;
    private final AuthorizationPort authorizationPort;

    public GetRoleUsersService(RoleRepositoryPort roleRepository,
                                UserRoleAssignmentRepositoryPort userRoleAssignmentRepository,
                                UserRepositoryPort userRepository,
                                AuthorizationPort authorizationPort) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.userRoleAssignmentRepository = Objects.requireNonNull(userRoleAssignmentRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Obtiene todos los usuarios asignados a un rol.
     *
     * <p>Verifica la existencia del rol, consulta las asignaciones y
     * recupera la información de cada usuario asignado.</p>
     *
     * @param roleId identificador único del rol
     * @return lista de respuestas con la información de los usuarios asignados
     * @throws NotFoundException si no existe un rol con el ID proporcionado
     */
    @Override
    public List<UserResponse> execute(UUID roleId) {
        Objects.requireNonNull(roleId);
        authorizationPort.requirePermission(Permission.ROLES_READ);

        roleRepository.findById(roleId)
            .orElseThrow(() -> new NotFoundException("Rol", roleId));

        log.debug("Retrieving users for role: {}", roleId);
        return userRoleAssignmentRepository.findByRoleId(roleId).stream()
            .map(UserRoleAssignment::getUserId)
            .map(userId -> userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId)))
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNombreCompleto(),
                user.getEstado().name(),
                user.getFechaCreacion(),
                user.getFechaActualizacion()
            ))
            .collect(Collectors.toList());
    }
}
