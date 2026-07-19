package ec.edu.espe.master_gateway.shared.infrastructure.security;

/**
 * Utilidad para extraer tokens JWT del encabezado de autorización HTTP.
 *
 * <p>Analiza el encabezado {@code Authorization} con formato {@code Bearer <token>}
 * y retorna el token si está presente, o vacío en caso contrario.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BearerTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public Optional<String> extract(String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }
}
