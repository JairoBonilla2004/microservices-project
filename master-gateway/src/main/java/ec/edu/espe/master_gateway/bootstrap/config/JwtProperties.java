package ec.edu.espe.master_gateway.bootstrap.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
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

    private final Duration tempTokenExpiration;

    private final Duration accessTokenExpiration;

    private final Duration refreshTokenExpiration;

    private final String secret;

    private final String privateKeyPem;

    private final String publicKeyPem;

    public JwtProperties(
            @DurationUnit(ChronoUnit.MINUTES) Duration tempTokenExpiration,
            @DurationUnit(ChronoUnit.MINUTES) Duration accessTokenExpiration,
            @DurationUnit(ChronoUnit.DAYS) Duration refreshTokenExpiration,
            String secret,
            @DefaultValue("") String privateKeyPem,
            @DefaultValue("") String publicKeyPem) {
        this.tempTokenExpiration = tempTokenExpiration;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.secret = secret;
        this.privateKeyPem = privateKeyPem;
        this.publicKeyPem = publicKeyPem;
    }

    public Duration getTempTokenExpiration() {
        return tempTokenExpiration;
    }

    public Duration getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Duration getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public String getSecret() {
        return secret;
    }

    public String getPrivateKeyPem() {
        return privateKeyPem;
    }

    public String getPublicKeyPem() {
        return publicKeyPem;
    }
}