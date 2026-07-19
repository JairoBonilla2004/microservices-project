package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.ListRolesUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
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
public class ListRolesService implements ListRolesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListRolesService.class);

    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public ListRolesService(RoleRepositoryPort roleRepository,
                            AuthorizationPort authorizationPort) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Obtiene todos los roles activos del sistema.
     *
     * <p>Recupera la lista de roles activos desde el repositorio y los
     * transforma en objetos {@link RoleResponse}.</p>
     *
     * @return lista de respuestas con la información de los roles activos
     */
    @Override
    public List<RoleResponse> execute() {
        authorizationPort.requirePermission(Permission.ROLES_READ);
        log.debug("Listing all active roles");
        return roleRepository.findAllActive().stream()
            .map(role -> new RoleResponse(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.getEstado().name(),
                role.getFechaCreacion()
            ))
            .collect(Collectors.toList());
    }
}
