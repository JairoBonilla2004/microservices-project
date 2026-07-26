package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de creación de roles.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code ROLES_CREATE} y que no exista un rol con el mismo nombre,
 * y luego persiste la entidad {@code Role} en el repositorio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class CreateRoleService implements CreateRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateRoleService.class);

    private final RoleRepositoryPort roleRepository;
    private final AuthorizationPort authorizationPort;

    public CreateRoleService(RoleRepositoryPort roleRepository,
                             AuthorizationPort authorizationPort) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public CreateRoleResponse execute(CreateRoleRequest request) {
        Objects.requireNonNull(request);
        authorizationPort.requirePermission(Permission.ROLES_CREATE);

        if (roleRepository.existsByNombre(request.nombre())) {
            throw new DuplicateException("Rol", "nombre", request.nombre());
        }

        String descripcion = request.descripcion() != null ? request.descripcion() : "";
        Role role = new Role(request.nombre(), descripcion);
        Role saved = roleRepository.save(role);

        log.info("Role created with id: {}, name: {}", saved.getId(), saved.getNombre());
        return new CreateRoleResponse(
            saved.getId(),
            saved.getNombre(),
            saved.getDescripcion(),
            saved.getFechaCreacion()
        );
    }
}
