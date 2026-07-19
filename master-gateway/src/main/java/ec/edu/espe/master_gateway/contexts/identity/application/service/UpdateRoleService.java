package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.UpdateRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de actualización de roles.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_UPDATE}, busca el rol por su identificador, aplica
 * las modificaciones solicitadas y persiste los cambios.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class UpdateRoleService implements UpdateRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateRoleService.class);

    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public UpdateRoleService(RoleRepositoryPort roleRepository,
                             AuthorizationPort authorizationPort) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public RoleResponse execute(UUID id, UpdateRoleRequest request) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(request);
        authorizationPort.requirePermission(Permission.ROLES_UPDATE);

        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Rol", id));

        if (request.nombre() != null) role.updateNombre(request.nombre());
        if (request.descripcion() != null) role.updateDescripcion(request.descripcion());

        Role saved = roleRepository.save(role);

        log.info("Role updated with id: {}, name: {}", saved.getId(), saved.getNombre());
        return new RoleResponse(
            saved.getId(),
            saved.getNombre(),
            saved.getDescripcion(),
            saved.getEstado().name(),
            saved.getFechaCreacion()
        );
    }
}
