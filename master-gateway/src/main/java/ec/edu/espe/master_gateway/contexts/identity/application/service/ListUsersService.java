package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.ListUsersUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
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
public class ListUsersService implements ListUsersUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListUsersService.class);

    private final UserRepositoryPort userRepository;
    private final AuthorizationPort authorizationPort;

    public ListUsersService(UserRepositoryPort userRepository,
                            AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Obtiene todos los usuarios activos del sistema.
     *
     * <p>Recupera la lista de usuarios activos desde el repositorio y
     * los transforma en objetos {@link UserResponse}.</p>
     *
     * @return lista de respuestas con la información de los usuarios activos
     */
    @Override
    public List<UserResponse> execute() {
        authorizationPort.requirePermission(Permission.USERS_READ);
        log.debug("Listing all active users");
        return userRepository.findAllActive().stream()
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
