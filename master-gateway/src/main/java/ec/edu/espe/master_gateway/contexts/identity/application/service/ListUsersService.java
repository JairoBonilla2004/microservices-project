package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.ListUsersUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.PageResult;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListUsersService implements ListUsersUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListUsersService.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepositoryPort userRepository;
    private final AuthorizationPort authorizationPort;

    public ListUsersService(UserRepositoryPort userRepository,
                            AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Obtiene una página de usuarios activos del sistema.
     *
     * @param page número de página (0-indexado, se normaliza a 0 si es negativo).
     * @param size tamaño de página (se acota entre 1 y {@value #MAX_PAGE_SIZE}).
     * @return página de respuestas con la información de los usuarios activos.
     */
    @Override
    public PageResult<UserResponse> execute(int page, int size) {
        authorizationPort.requirePermission(Permission.USERS_READ);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        log.debug("Listing active users, page={}, size={}", safePage, safeSize);
        var result = userRepository.findActivePage(safePage, safeSize);

        var content = result.content().stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNombreCompleto(),
                user.getEstado().name(),
                user.getFechaCreacion(),
                user.getFechaActualizacion()
            ))
            .toList();

        return new PageResult<>(content, result.totalElements(), safePage, safeSize);
    }
}
