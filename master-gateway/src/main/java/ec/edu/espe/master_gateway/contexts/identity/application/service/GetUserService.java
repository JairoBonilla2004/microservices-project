package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetUserService implements GetUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetUserService.class);

    private final UserRepositoryPort userRepository;
    private final AuthorizationPort authorizationPort;

    public GetUserService(UserRepositoryPort userRepository,
                          AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Obtiene la información de un usuario por su identificador.
     *
     * <p>Recupera el usuario desde el repositorio y lo convierte en un
     * objeto {@link UserResponse} con todos sus datos.</p>
     *
     * @param id identificador único del usuario
     * @return respuesta con la información del usuario solicitado
     * @throws NotFoundException si no existe un usuario con el ID proporcionado
     */
    @Override
    public UserResponse execute(UUID id) {
        Objects.requireNonNull(id);
        authorizationPort.requireOwnershipOrPermission(id, Permission.USERS_READ);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario", id));

        log.debug("Retrieving user with id: {}", id);
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getNombreCompleto(),
            user.getEstado().name(),
            user.getFechaCreacion(),
            user.getFechaActualizacion()
        );
    }
}
