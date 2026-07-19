package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.DeactivateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de desactivación de usuarios.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code USERS_DELETE}, busca el usuario por su identificador y lo
 * desactiva, impidiendo su acceso futuro al sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class DeactivateUserService implements DeactivateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeactivateUserService.class);

    private final UserRepositoryPort userRepository;
    private final AuthorizationPort authorizationPort;

    public DeactivateUserService(UserRepositoryPort userRepository,
                                 AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID id) {
        Objects.requireNonNull(id);
        authorizationPort.requirePermission(Permission.USERS_DELETE);

        UUID currentUserId = authorizationPort.getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario", id));

        user.deactivate();
        userRepository.save(user);
        log.info("User deactivated with id: {}", id);
    }
}
