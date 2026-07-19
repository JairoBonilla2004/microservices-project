package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.security;

/**
 * Adaptador de hashing de contraseñas utilizando el algoritmo Argon2.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort}
 * utilizando {@link org.springframework.security.crypto.argon2.Argon2PasswordEncoder}
 * de Spring Security. Se activa únicamente cuando la propiedad
 * {@code password.policy.hash-algorithm} tiene el valor {@code argon2}.</p>
 *
 * <p>Argon2 es el ganador de la competición de hashing de contraseñas PHC
 * (Password Hashing Competition) y se considera más resistente a ataques
 * con hardware especializado (GPU/ASIC) que BCrypt. El factor de costo se
 * configura mediante la propiedad {@code password.policy.cost-factor}
 * (por defecto 2).</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "password.policy.hash-algorithm", havingValue = "argon2") // sirve para activar este bean solo si la propiedad tiene el valor "argon2"
public class Argon2PasswordHasherAdapter implements PasswordHasherPort {

    private final Argon2PasswordEncoder encoder;

    public Argon2PasswordHasherAdapter(
            @Value("${password.policy.cost-factor:2}") int costFactor) {
        this.encoder = new Argon2PasswordEncoder(16, 32, costFactor, 16384, 3);
    }

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contrasena no puede ser nula o vacia");
        }
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        return encoder.matches(rawPassword, hashedPassword);
    }
}
