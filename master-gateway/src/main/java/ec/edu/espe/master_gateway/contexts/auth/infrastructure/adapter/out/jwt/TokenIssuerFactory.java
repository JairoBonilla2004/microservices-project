package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt;

/**
 * Fábrica para seleccionar el validador de tokens JWT según el modo
 * de validación del servicio destino.
 *
 * <p>Implementa el patrón <b>Factory</b> (GoF) para resolver en tiempo de
 * ejecución qué implementación de {@link TokenValidationPort}
 * utilizar, basándose en el {@link ValidationMode}
 * del microservicio hijo registrado.</p>
 *
 * <p>Para los modos {@code DELEGATE} y {@code LOCAL} se utiliza el validador
 * por defecto (que valida con la clave del Gateway). La única diferencia es
 * que en modo {@code LOCAL} el microservicio obtiene la clave pública del
 * Gateway para validar localmente sin llamar a este endpoint.</p>
 *
 * <p>El modo {@code NONE} también usa el validador por defecto pero sin
 * exigir ninguna verificación adicional del lado del Gateway.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService.ValidationMode;
import org.springframework.stereotype.Component;

@Component
public class TokenIssuerFactory {

    private final TokenValidationPort defaultValidator;

    public TokenIssuerFactory(TokenValidationPort defaultValidator) {
        this.defaultValidator = defaultValidator;
    }

    public TokenValidationPort getValidator(ValidationMode mode) {
        return switch (mode) {
            case LOCAL, DELEGATE, NONE -> defaultValidator;
        };
    }

    public TokenValidationPort getDefaultValidator() {
        return defaultValidator;
    }
}
