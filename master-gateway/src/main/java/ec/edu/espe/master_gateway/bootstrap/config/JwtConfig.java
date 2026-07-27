package ec.edu.espe.master_gateway.bootstrap.config;

import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtIssuerAdapter;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtValidatorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "jwt.mode", havingValue = "asymmetric")
public class JwtConfig {

    private final RevokedTokenRepositoryPort revokedTokenRepositoryPort;

    public JwtConfig(RevokedTokenRepositoryPort revokedTokenRepositoryPort) {
        this.revokedTokenRepositoryPort = revokedTokenRepositoryPort;
    }

    @Bean
    public TokenValidationPort asymmetricTokenValidator(AsymmetricJwtIssuerAdapter issuer,
                                                         JwtProperties jwtProperties) {
        var publicKeyPem = jwtProperties.getPublicKeyPem();
        if (publicKeyPem == null || publicKeyPem.isBlank()) {
            publicKeyPem = issuer.getPublicKeyPem();
        }
        return new AsymmetricJwtValidatorAdapter(publicKeyPem, revokedTokenRepositoryPort);
    }
}
