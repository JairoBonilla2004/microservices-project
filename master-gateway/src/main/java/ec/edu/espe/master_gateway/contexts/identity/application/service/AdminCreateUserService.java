package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AdminCreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de creación de usuarios con autorización administrativa.
 *
 * <p>Implementa el flujo de creación de usuarios por parte de un
 * administrador. Verifica que el usuario autenticado posea el permiso
 * {@code USERS_CREATE} antes de delegar la creación en el caso de uso
 * {@code CreateUserUseCase}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class AdminCreateUserService implements AdminCreateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(AdminCreateUserService.class);

    private final CreateUserUseCase createUserUseCase;
    private final AuthorizationPort authorizationPort;

    public AdminCreateUserService(CreateUserUseCase createUserUseCase,
                                  AuthorizationPort authorizationPort) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public CreateUserResponse execute(CreateUserRequest request) {
        authorizationPort.requirePermission(Permission.USERS_CREATE);
        log.info("Admin creating user with username: {}", request.username());
        return createUserUseCase.execute(request);
    }
}
