package ec.edu.espe.master_gateway.contexts.auth.domain.port.out;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida del dominio para la persistencia de refresh tokens.
 *
 * <p>Define las operaciones requeridas por el dominio para almacenar,
 * recuperar y revocar tokens de actualización, sin depender de una
 * tecnología de persistencia específica. Su implementación corresponde
 * a la capa de infraestructura, permitiendo aplicar el principio de
 * inversión de dependencias propuesto por la Arquitectura Hexagonal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeByUserId(UUID userId);
}