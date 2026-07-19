package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.security;

/**
 * Adaptador de hashing de contraseñas utilizando el algoritmo BCrypt.
 *
 * <p>Implementa {@link ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort}
 * utilizando {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
 * de Spring Security. Es la implementación predeterminada del sistema y se
 * activa cuando la propiedad {@code password.policy.hash-algorithm} tiene el
 * valor {@code bcrypt} o no está configurada.</p>
 *
 * <p>El factor de costo se configura mediante la propiedad
 * {@code password.policy.cost-factor} (por defecto 12). Un factor más alto
 * hace que el hashing sea más lento y más resistente a ataques de fuerza
 * bruta, pero aumenta el tiempo de procesamiento.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "password.policy.hash-algorithm", havingValue = "bcrypt", matchIfMissing = true) // sirve para activar este bean solo si la propiedad tiene el valor "bcrypt" o si no está configurada
public class BcryptPasswordHasherAdapter implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder;

    public BcryptPasswordHasherAdapter(@Value("${password.policy.cost-factor:12}") int costFactor) {
        this.encoder = new BCryptPasswordEncoder(costFactor);
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
