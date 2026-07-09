package ec.edu.espe.master_gateway.bootstrap.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

/**
 * Propiedades de configuración para el control de intentos de autenticación.
 *
 * <p>Permite definir los parámetros utilizados para limitar los intentos de
 * inicio de sesión mediante Rate Limiting. Las propiedades son cargadas
 * automáticamente desde la configuración de la aplicación utilizando el
 * prefijo {@code rate-limiting.login}.</p>
 *
 * <p>Esta configuración permite establecer la cantidad máxima de intentos de
 * autenticación permitidos, el período de tiempo en el que se contabilizan
 * dichos intentos y el criterio utilizado para identificar al cliente que
 * realiza las solicitudes.</p>
 *
 * <p>Su objetivo principal es ayudar a mitigar ataques de fuerza bruta contra
 * el mecanismo de autenticación, evitando múltiples intentos consecutivos de
 * acceso con credenciales inválidas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@ConfigurationProperties(prefix = "rate-limiting.login")
public class LoginRateLimitProperties {

    /**
     * Cantidad máxima de intentos de autenticación permitidos dentro de la
     * ventana de tiempo configurada.
     */
    private final int maxAttempts;

    /**
     * Duración del período utilizado para contabilizar los intentos de inicio
     * de sesión.
     */
    private final Duration windowDuration;

    /**
     * Tipo de clave utilizada para identificar los intentos de autenticación.
     *
     * <p>Permite definir el criterio utilizado para aplicar la restricción,
     * por ejemplo, considerando únicamente la dirección IP o combinando la
     * IP con el nombre de usuario.</p>
     */
    private final String keyType;

    /**
     * Inicializa las propiedades de configuración del límite de intentos de
     * autenticación.
     *
     * @param maxAttempts cantidad máxima de intentos de login permitidos.
     * @param windowDuration período de tiempo durante el cual se contabilizan
     *                       los intentos realizados.
     * @param keyType criterio utilizado para identificar al cliente que realiza
     *                los intentos de autenticación.
     */
    public LoginRateLimitProperties(
            @DefaultValue("5") int maxAttempts,
            @DurationUnit(ChronoUnit.MINUTES) @DefaultValue("1") Duration windowDuration,
            @DefaultValue("ip_and_username") String keyType) {
        this.maxAttempts = maxAttempts;
        this.windowDuration = windowDuration;
        this.keyType = keyType;
    }

    /**
     * Obtiene la cantidad máxima de intentos de autenticación permitidos.
     *
     * @return número máximo de intentos configurados.
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    /**
     * Obtiene la duración de la ventana de control de intentos.
     *
     * @return período de tiempo utilizado para reiniciar el contador de intentos.
     */
    public Duration getWindowDuration() {
        return windowDuration;
    }

    /**
     * Obtiene el tipo de clave utilizada para aplicar la limitación.
     *
     * @return criterio de identificación del cliente para el Rate Limiter.
     */
    public String getKeyType() {
        return keyType;
    }
}