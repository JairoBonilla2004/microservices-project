package ec.edu.espe.master_gateway.bootstrap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI del microservicio.
 *
 * <p>Define la información general expuesta por la especificación OpenAPI,
 * utilizada por herramientas como Swagger UI para generar la documentación
 * interactiva de la API REST. La configuración incluye el nombre del
 * microservicio, una descripción de su propósito, la versión de la API
 * y la información de la licencia del proyecto.</p>
 *
 * <p>El objeto {@link OpenAPI} registrado como bean es detectado
 * automáticamente por SpringDoc para construir la documentación accesible
 * desde la interfaz web de Swagger.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Configuration
public class OpenApiConfig {

    /**
     * Crea la configuración principal de OpenAPI para el microservicio.
     *
     * <p>Define los metadatos que serán mostrados en la documentación
     * interactiva de la API, incluyendo el título, la descripción,
     * la versión y la licencia del proyecto.</p>
     *
     * @return configuración de OpenAPI utilizada para generar la
     *         documentación de la API REST.
     */
    @Bean
    public OpenAPI masterGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Master Gateway API")
                        .description("Microservicio maestro de autenticación y autorización centralizada")
                        .version("1.0.0")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}