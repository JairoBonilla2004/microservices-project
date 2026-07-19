package ec.edu.espe.master_gateway.bootstrap.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

/**
 * Propiedades de configuración para el límite de solicitudes de la API.
 *
 * <p>Permite gestionar los parámetros utilizados para configurar el control
 * de tráfico de los endpoints generales del microservicio mediante Rate
 * Limiting. Las propiedades son cargadas automáticamente desde la
 * configuración de la aplicación utilizando el prefijo
 * {@code rate-limiting.api}.</p>
 *
 * <p>Define la cantidad máxima de solicitudes permitidas y el período de
 * tiempo durante el cual se aplica dicho límite. En caso de no existir una
 * configuración personalizada, se utilizan valores predeterminados para
 * garantizar un comportamiento seguro de la aplicación.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@ConfigurationProperties(prefix = "rate-limiting.api")
public class ApiRateLimitProperties {

    /**
     * Cantidad máxima de solicitudes permitidas dentro del período configurado.
     */
    private final int maxRequests;

    /**
     * Tiempo de duración de la ventana de limitación de solicitudes.
     */
    private final Duration windowDuration;

    /**
     * Inicializa las propiedades de configuración del límite de solicitudes.
     *
     * @param maxRequests cantidad máxima de solicitudes permitidas en la ventana
     *                    de tiempo configurada.
     * @param windowDuration duración del período de renovación del límite de
     *                       solicitudes.
     */
    public ApiRateLimitProperties(
            @DefaultValue("100") int maxRequests,
            @DurationUnit(ChronoUnit.MINUTES) @DefaultValue("1m") Duration windowDuration) {
        this.maxRequests = maxRequests;
        this.windowDuration = windowDuration;
    }

    /**
     * Obtiene la cantidad máxima de solicitudes permitidas.
     *
     * @return número máximo de solicitudes configurado.
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Obtiene la duración de la ventana de limitación.
     *
     * @return período de tiempo utilizado para reiniciar el contador de
     *         solicitudes.
     */
    public Duration getWindowDuration() {
        return windowDuration;
    }
}