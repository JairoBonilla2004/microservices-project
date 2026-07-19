package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetRolePermissionsUseCase;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
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
public class GetRolePermissionsService implements GetRolePermissionsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetRolePermissionsService.class);

    private final RolePermissionAssignmentRepositoryPort assignmentRepository;
    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public GetRolePermissionsService(RolePermissionAssignmentRepositoryPort assignmentRepository,
                                      RoleRepositoryPort roleRepository,
                                      AuthorizationPort authorizationPort) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public List<Permission> execute(UUID roleId) {
        Objects.requireNonNull(roleId);
        authorizationPort.requirePermission(Permission.ROLES_READ);

        roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Rol", roleId));

        log.debug("Retrieving permissions for role: {}", roleId);
        return assignmentRepository.findPermissionsByRoleId(roleId);
    }
}
