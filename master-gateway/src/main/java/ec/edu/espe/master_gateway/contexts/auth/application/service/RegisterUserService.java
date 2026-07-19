package ec.edu.espe.master_gateway.contexts.auth.application.service;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.RegisterUserUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.shared.domain.InvalidInputException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

    private final CreateUserUseCase createUserUseCase;

    public RegisterUserService(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
    }

    @Override
    public RegisterUserResponse execute(RegisterUserRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            log.warn("Registration failed: passwords do not match for username: {}", request.username());
            throw new InvalidInputException("Las contraseñas no coinciden");
        }
        var created = createUserUseCase.execute(
                new CreateUserRequest(
                        request.username(),
                        request.email(),
                        request.password(),
                        request.nombreCompleto()
                )
        );
        log.info("User registered successfully: {}", created.username());
        log.debug("User {} registered without default role. An admin must assign roles manually.", created.username());
        return new RegisterUserResponse(
                created.id(),
                created.username(),
                created.email(),
                created.nombreCompleto()
        );
    }
}
