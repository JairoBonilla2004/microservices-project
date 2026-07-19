package ec.edu.espe.master_gateway.bootstrap.config;

/**
 * Propiedades de configuración para el control de intentos de registro.
 *
 * <p>Permite definir los parámetros utilizados para limitar los intentos de
 * registro de usuarios mediante Rate Limiting. Las propiedades son cargadas
 * automáticamente desde la configuración de la aplicación utilizando el prefijo
 * {@code rate-limiting.register}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "rate-limiting.register")
public class RegisterRateLimitProperties {

    private final int maxAttempts;
    private final Duration windowDuration;

    public RegisterRateLimitProperties(
            @DefaultValue("5") int maxAttempts,
            @DurationUnit(ChronoUnit.MINUTES) @DefaultValue("1") Duration windowDuration) {
        this.maxAttempts = maxAttempts;
        this.windowDuration = windowDuration;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public Duration getWindowDuration() {
        return windowDuration;
    }
}
