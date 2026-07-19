package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.InvalidInputException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUserService implements CreateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateUserService.class);

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    public CreateUserService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    @Override
    public CreateUserResponse execute(CreateUserRequest request) {
        Objects.requireNonNull(request);

        sanitizeInput(request.username(), "username");
        sanitizeInput(request.email(), "email");
        sanitizeInput(request.nombreCompleto(), "nombreCompleto");

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateException("Usuario", "username", request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateException("Usuario", "email", request.email());
        }

        String passwordHash = passwordHasher.hash(request.password());

        User user = new User(request.username(), request.email(), passwordHash, request.nombreCompleto());
        User saved = userRepository.save(user);

        log.info("User created with id: {}, username: {}", saved.getId(), saved.getUsername());
        return new CreateUserResponse(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getNombreCompleto(),
            saved.getFechaCreacion()
        );
    }

    private void sanitizeInput(String value, String fieldName) {
        if (value != null && (value.contains("<") || value.contains(">"))) {
            throw new InvalidInputException(
                "El campo " + fieldName + " no puede contener caracteres HTML"
            );
        }
    }
}
