package ec.edu.espe.master_gateway.bootstrap.config;

import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans por defecto para puertos de dominio que aún no tienen
 * implementación concreta. Estos beans son reemplazados automáticamente cuando
 * las fases posteriores (Fase 05) registran sus propias implementaciones.
 */
@Configuration
public class DefaultBeansConfig {

    @Bean
    @ConditionalOnMissingBean
    public TokenValidationPort defaultTokenValidationPort() {
        return new TokenValidationPort() {
            @Override
            public TokenClaims validate(String token) {
                throw new UnsupportedOperationException(
                        "TokenValidationPort no implementado — Fase 05 requerida");
            }

            @Override
            public TokenClaims validateTempToken(String token) {
                throw new UnsupportedOperationException(
                        "TokenValidationPort no implementado — Fase 05 requerida");
            }

            @Override
            public void invalidateTempToken(String token) {
                throw new UnsupportedOperationException(
                        "TokenValidationPort no implementado — Fase 05 requerida");
            }
        };
    }
}
