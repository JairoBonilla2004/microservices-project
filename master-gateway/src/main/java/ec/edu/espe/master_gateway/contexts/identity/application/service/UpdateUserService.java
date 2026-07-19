package ec.edu.espe.master_gateway.contexts.identity.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.UpdateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.InvalidInputException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de actualización de usuarios.
 *
 * <p>Verifica que el usuario autenticado tenga el permiso
 * {@code USERS_UPDATE} o sea el propietario del perfil. Permite
 * actualizar email, nombre completo y contraseña, validando la
 * contraseña actual cuando se solicita un cambio de la misma.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class UpdateUserService implements UpdateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserService.class);

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final AuthorizationPort authorizationPort;

    public UpdateUserService(UserRepositoryPort userRepository,
                             PasswordHasherPort passwordHasher,
                             AuthorizationPort authorizationPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public UpdateUserResponse execute(UUID id, UpdateUserRequest request) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(request);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario", id));

        authorizationPort.requireOwnershipOrPermission(id, Permission.USERS_UPDATE);

        if (request.email() != null) {
            user.updateEmail(request.email());
        }
        if (request.nombreCompleto() != null) {
            user.updateNombreCompleto(request.nombreCompleto());
        }

        if (request.newPassword() != null) {
            if (request.currentPassword() == null) {
                throw new InvalidInputException("Debe proporcionar la contraseña actual para cambiarla");
            }
            if (!passwordHasher.matches(request.currentPassword(), user.getPasswordHash())) {
                throw new InvalidInputException("La contraseña actual no es correcta");
            }
            String hashedPassword = passwordHasher.hash(request.newPassword());
            user.updatePassword(hashedPassword);
        }

        User saved = userRepository.save(user);

        log.info("User updated with id: {}", saved.getId());
        return new UpdateUserResponse(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getNombreCompleto(),
            saved.getEstado().name(),
            saved.getFechaActualizacion()
        );
    }
}
