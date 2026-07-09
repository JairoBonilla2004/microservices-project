package ec.edu.espe.master_gateway.bootstrap.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuración de auditoría para JPA.
 *
 * <p>Habilita el mecanismo de auditoría de Spring Data JPA y define el
 * proveedor encargado de obtener el usuario responsable de las operaciones
 * de persistencia. El usuario autenticado se recupera desde el contexto de
 * seguridad de Spring Security y es utilizado automáticamente por las
 * entidades que implementan auditoría mediante anotaciones como
 * {@code @CreatedBy} y {@code @LastModifiedBy}.</p>
 *
 * <p>Cuando no existe un usuario autenticado, se asigna el valor
 * {@code "SYSTEM"} como identificador del responsable de la operación.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Configuration
@EnableJpaAuditing
public class AuditorAwareConfig {

    /**
     * Proporciona la implementación de {@link AuditorAware} utilizada por
     * Spring Data JPA para identificar al usuario que realiza operaciones
     * de creación o modificación sobre las entidades.
     *
     * @return proveedor del nombre del usuario autenticado o {@code "SYSTEM"}
     *         cuando no existe autenticación activa.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }

            return Optional.of(authentication.getName());
        };
    }
}