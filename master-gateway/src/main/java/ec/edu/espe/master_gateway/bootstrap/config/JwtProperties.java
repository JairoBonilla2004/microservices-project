package ec.edu.espe.master_gateway.bootstrap.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

/**
 * Propiedades de configuración para la gestión de tokens JWT.
 *
 * <p>Asocia automáticamente los parámetros definidos con el prefijo
 * {@code jwt} en los archivos de configuración de la aplicación
 * ({@code application.yml} o {@code application.properties}). Estas
 * propiedades incluyen los tiempos de expiración de los diferentes tipos
 * de tokens y la clave secreta utilizada para su firma y validación.</p>
 *
 * <p>Los tiempos de expiración son convertidos automáticamente a objetos
 * {@link Duration}, permitiendo representar intervalos de tiempo de forma
 * segura y consistente dentro de la aplicación.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Tiempo de expiración de los tokens temporales.
     */
    private final Duration tempTokenExpiration;

    /**
     * Tiempo de expiración de los tokens de acceso.
     */
    private final Duration accessTokenExpiration;

    /**
     * Tiempo de expiración de los tokens de actualización.
     */
    private final Duration refreshTokenExpiration;

    /**
     * Clave secreta utilizada para firmar y validar los JWT.
     */
    private final String secret;

    /**
     * Inicializa las propiedades de configuración de JWT.
     *
     * @param tempTokenExpiration tiempo de expiración de los tokens temporales,
     *                            expresado en minutos.
     * @param accessTokenExpiration tiempo de expiración de los tokens de acceso,
     *                              expresado en minutos.
     * @param refreshTokenExpiration tiempo de expiración de los tokens de
     *                               actualización, expresado en días.
     * @param secret clave secreta utilizada para la firma y validación de los
     *               tokens JWT.
     */
    public JwtProperties( //Lee el valor de la propiedad en el archivo de configuración, Spring Boot lo convertirá automáticamente a un objeto Duration, interpretando el valor como minutos o días.
            @DurationUnit(ChronoUnit.MINUTES) Duration tempTokenExpiration,
            @DurationUnit(ChronoUnit.MINUTES) Duration accessTokenExpiration,
            @DurationUnit(ChronoUnit.DAYS) Duration refreshTokenExpiration,
            String secret) {
        this.tempTokenExpiration = tempTokenExpiration;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.secret = secret;
    }

    /**
     * Obtiene el tiempo de expiración del token temporal.
     *
     * @return duración configurada para el token temporal.
     */
    public Duration getTempTokenExpiration() {
        return tempTokenExpiration;
    }

    /**
     * Obtiene el tiempo de expiración del token de acceso.
     *
     * @return duración configurada para el token de acceso.
     */
    public Duration getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Obtiene el tiempo de expiración del token de actualización.
     *
     * @return duración configurada para el token de actualización.
     */
    public Duration getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    /**
     * Obtiene la clave secreta utilizada para la firma y validación de los JWT.
     *
     * @return clave secreta configurada.
     */
    public String getSecret() {
        return secret;
    }
}