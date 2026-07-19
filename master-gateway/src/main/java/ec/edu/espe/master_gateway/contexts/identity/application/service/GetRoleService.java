package ec.edu.espe.master_gateway.contexts.identity.application.service;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetRoleService implements GetRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetRoleService.class);

    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public GetRoleService(RoleRepositoryPort roleRepository,
                          AuthorizationPort authorizationPort) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public RoleResponse execute(UUID id) {
        Objects.requireNonNull(id);
        authorizationPort.requirePermission(Permission.ROLES_READ);
        var role = roleRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Rol", id));
        log.debug("Role found: id={}, name={}", id, role.getNombre());
        return new RoleResponse(
            role.getId(),
            role.getNombre(),
            role.getDescripcion(),
            role.getEstado().name(),
            role.getFechaCreacion()
        );
    }
}
