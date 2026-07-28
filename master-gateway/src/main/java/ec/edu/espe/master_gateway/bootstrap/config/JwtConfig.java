package ec.edu.espe.master_gateway.bootstrap.config;

import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtIssuerAdapter;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtValidatorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración encargada de registrar los componentes relacionados con la
 * validación de JSON Web Tokens (JWT) utilizando criptografía asimétrica.
 *
 * <p>Esta configuración solo se activa cuando la propiedad:</p>
 *
 * <pre>
 * jwt.mode=asymmetric
 * </pre>
 *
 * <p>En este modo, la validación de los tokens se realiza mediante una clave
 * pública, permitiendo que los microservicios verifiquen la autenticidad del
 * JWT sin necesidad de compartir una clave secreta.</p>
 *
 * <p>Además, se integra con el repositorio de tokens revocados para impedir
 * el uso de tokens invalidados antes de su fecha de expiración.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche

 */
@Configuration
@ConditionalOnProperty(name = "jwt.mode", havingValue = "asymmetric")
public class JwtConfig {

    /**
     * Repositorio utilizado para verificar si un token ha sido revocado.
     */
    private final RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    public JwtConfig(RevokedTokenRepositoryPort revokedTokenRepositoryPort) {
        this.revokedTokenRepositoryPort = revokedTokenRepositoryPort;
    }

    /**
     * Registra el bean responsable de validar tokens JWT firmados mediante
     * criptografía asimétrica.
     *
     * <p>El proceso de obtención de la clave pública sigue el siguiente orden:</p>
     * <ol>
     *     <li>Se intenta obtener la clave pública desde la configuración
     *     ({@link JwtProperties}).</li>
     *     <li>Si no existe una clave configurada, se recupera desde el
     *     emisor de tokens ({@link AsymmetricJwtIssuerAdapter}).</li>
     * </ol>
     *
     * <p>El validador resultante verifica:</p>
     * <ul>
     *     <li>La firma digital del JWT.</li>
     *     <li>La integridad del contenido.</li>
     *     <li>La expiración del token.</li>
     *     <li>La revocación del token.</li>
     * </ul>
     *
     * @param issuer componente encargado de proporcionar la clave pública
     *               utilizada para validar los JWT.
     * @param jwtProperties propiedades de configuración relacionadas con JWT.
     * @return implementación de {@link TokenValidationPort} para validación
     *         de tokens asimétricos.
     */
    @Bean
    public TokenValidationPort asymmetricTokenValidator(
            AsymmetricJwtIssuerAdapter issuer,
            JwtProperties jwtProperties) {

        var publicKeyPem = jwtProperties.getPublicKeyPem();

        if (publicKeyPem == null || publicKeyPem.isBlank()) {
            publicKeyPem = issuer.getPublicKeyPem();
        }

        return new AsymmetricJwtValidatorAdapter(
                publicKeyPem,
                revokedTokenRepositoryPort
        );
    }
}